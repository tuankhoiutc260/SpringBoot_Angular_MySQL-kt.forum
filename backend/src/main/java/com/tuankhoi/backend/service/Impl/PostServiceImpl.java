package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PostMapper;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.repository.Elasticsearch.PostElasticsearchRepository;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.repository.Jpa.SubCategoryRepository;
import com.tuankhoi.backend.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class PostServiceImpl implements PostService {
    SubCategoryRepository subCategoryRepository;

    PostRepository postRepository;
    PostElasticsearchRepository postElasticsearchRepository;

    PostMapper postMapper;

    ElasticsearchOperations elasticsearchOperations;

    //    @PostAuthorize("@userServiceImpl.getById(returnObject.createdBy).userName == authentication.name")
    @Override
    public PostResponse create(PostRequest postRequest) {
        try {
            log.warn(postRequest.toString());
            Post newPost = postMapper.toPost(postRequest);
            Post savedPost = postRepository.save(newPost);

            PostDocument postDocument = postMapper.toPostDocument(newPost);
            indexPost(postDocument);

            return postMapper.toPostResponse(savedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Create Post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Create Post: " + e.getMessage(), e);
        }
    }

    @Override
    public PostResponse getById(String postId) {
        return postRepository.findById(postId)
                .map(postMapper::toPostResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Override
    public List<PostResponse> getBySubCategoryId(String subCategoryId, int page, int size) {
        if (subCategoryId == null || subCategoryId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_SUB_CATEGORY_ID);
        }

        Pageable pageable = PageRequest.of(page, size);

        SubCategory existingPSubCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        List<Post> postResponseList = postRepository
                .findBySubCategoryId(existingPSubCategory.getId(), pageable);

        return postResponseList.stream()
                .map(postMapper::toPostResponse)
                .collect(Collectors.toList());
    }

//    @PostAuthorize("@userServiceImpl.getById(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @Override
    public PostResponse update(String id, PostRequest postRequest) {
        log.warn(postRequest.toString());
        try {
            Post existingPost = postRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

            postMapper.updatePostFromRequest(postRequest, existingPost);
            Post updatedPost = postRepository.save(existingPost);

            PostDocument postDocument = postMapper.toPostDocument(updatedPost);
            indexPost(postDocument);

            return postMapper.toPostResponse(updatedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Failed to Update Post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Update Post", e);
        }
    }

//    @PostAuthorize("@userServiceImpl.getByUserName(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @Override
    public void deleteById(String postId) {
        try {
            Post postToDelete = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

            postRepository.deleteById(postToDelete.getId());
            postElasticsearchRepository.deleteById(postToDelete.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Delete Post due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Delete Post", e);
        }
    }

    @Override
    public List<PostResponse> getAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::toPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> search(String query) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields("title", "content")
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .build();

        SearchHits<PostDocument> searchHits = elasticsearchOperations.search(searchQuery, PostDocument.class);
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    PostDocument postDocument = hit.getContent();
                    Post post = postRepository.findById(postDocument.getId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
                    return postMapper.toPostResponse(post);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(String postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);

            PostDocument postDocument = postElasticsearchRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            postElasticsearchRepository.save(postDocument);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to increase view count", e);
        }
    }

    @Override
    public void indexPost(PostDocument postDocument) {
        try {
            postElasticsearchRepository.save(postDocument);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Index Post in Elasticsearch: ", e);
        }
    }
}
