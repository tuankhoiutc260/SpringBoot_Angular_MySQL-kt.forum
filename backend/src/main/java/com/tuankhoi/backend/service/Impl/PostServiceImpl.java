package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
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
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {
    static final String ELASTICSEARCH_TITLE_FIELD = "title";
    static final String ELASTICSEARCH_CONTENT_FIELD = "content";

    SubCategoryRepository subCategoryRepository;
    PostRepository postRepository;
    PostElasticsearchRepository postElasticsearchRepository;
    PostMapper postMapper;
    ElasticsearchOperations elasticsearchOperations;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "post", allEntries = true),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true)
    })
    @Override
    public PostResponse create(PostRequest postRequest) {
        try {
            Post newPost = postMapper.toPost(postRequest);
            Post savedPost = postRepository.save(newPost);

            indexPost(postMapper.toPostDocument(newPost));

            return postMapper.toPostResponse(savedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create Post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create Post: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "post", key = "#postId", unless = "#result == null")
    @Override
    public PostResponse getById(String postId) {
        return postRepository.findById(postId)
                .map(postMapper::toPostResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Cacheable(value = "posts", key = "'subCategoryId:' + #subCategoryId + ',page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<PostResponse> getBySubCategoryId(String subCategoryId, int page, int size) {
        if (subCategoryId == null || subCategoryId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_SUB_CATEGORY_ID);
        }
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGINATION_PARAMETERS);
        }

        Pageable pageable = PageRequest.of(page, size);

        SubCategory existingPSubCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        Page<Post> postPage = postRepository
                .findBySubCategoryId(existingPSubCategory.getId(), pageable);

        return postPage.map(postMapper::toPostResponse);
    }

    @Cacheable(value = "posts", key = "'all:page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<PostResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(postMapper::toPostResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "post", key = "#postId"),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true)
    })
    @Override
    public PostResponse update(String postId, PostRequest postRequest) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

        try {
            postMapper.updatePostFromRequest(postRequest, existingPost);
            Post updatedPost = postRepository.save(existingPost);

            indexPost(postMapper.toPostDocument(updatedPost));

            return postMapper.toPostResponse(updatedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Failed to Update Post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update Post", e);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "post", key = "#postId"),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true)
    })
    @Override
    public void deleteById(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOTFOUND);
        }

        try {
            postRepository.deleteById(postId);
            deletePostFromElasticsearch(postId);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete Post due to database constraint", e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Post: " + e.getMessage());
        }
    }

    @Cacheable(value = "postSearch", key = "'query:' + #query + ',page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<PostResponse> search(String query, int page, int size) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields(ELASTICSEARCH_TITLE_FIELD, ELASTICSEARCH_CONTENT_FIELD)
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .build();

        SearchHits<PostDocument> searchHits = elasticsearchOperations.search(searchQuery, PostDocument.class);

        List<PostResponse> postResponseList = searchHits.getSearchHits().stream()
                .map(hit -> postMapper.toPostResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());

        long totalHits = searchHits.getTotalHits();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(postResponseList, pageable, totalHits);
    }

    @Caching(evict = {
            @CacheEvict(value = "post", key = "#postId"),
            @CacheEvict(value = "posts", allEntries = true)
    })
    @Override
    public void incrementViewCount(String postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);

            PostDocument postDocument = postElasticsearchRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            postDocument.setViewCount(post.getViewCount());
            postElasticsearchRepository.save(postDocument);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to increase view count", e);
        }
    }

    @Override
    public void indexPost(PostDocument postDocument) {
        try {
            postElasticsearchRepository.save(postDocument);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_INDEXING_ERROR, "Failed to Index Sub Category in Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Index Sub Category in Elasticsearch: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void deletePostFromElasticsearch(String postId) {
        try {
            postElasticsearchRepository.deleteById(postId);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_ERROR, "Failed to Delete Post from Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Post from Elasticsearch: " + e.getMessage());
        }
    }
}
