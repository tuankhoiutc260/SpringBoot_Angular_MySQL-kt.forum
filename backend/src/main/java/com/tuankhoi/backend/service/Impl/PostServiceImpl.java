package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PostMapper;
import com.tuankhoi.backend.model.entity.Post;
//import com.tuankhoi.backend.repository.ElasticSearch.IElasticsearchPostRepository;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.repository.Jpa.SubCategoryRepository;
import com.tuankhoi.backend.service.IPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements IPostService {
    private final SubCategoryRepository subCategoryRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

//    private final IElasticsearchPostRepository iElasticsearchPostRepository;

    //    @PostAuthorize("@userServiceImpl.findById(returnObject.createdBy).userName == authentication.name")
    @Override
    public PostResponse create(PostRequest postRequest) {
        try {
            log.warn(postRequest.toString());
            Post newPost = postMapper.toPost(postRequest);
            Post savedPost = postRepository.save(newPost);
            return postMapper.toPostResponseWithCountLikes(savedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Failed to create post due to database constraint: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to create post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to create post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }


    @Override
    public List<PostResponse> findAll() {


        return postRepository.findAll(Sort.by(Sort.Order.asc("createdDate")))
                .stream()
                .map(postMapper::toPostResponseWithCountLikes)
                .toList();
    }

    @Override
    public List<PostResponse> findTop10ByOrderByLikesDesc() {
        return postRepository.findTop10ByOrderByLikesDesc().stream().map(postMapper::toPostResponseWithCountLikes).toList();
    }

    @Override
    public PostResponse findById(String postId) {
        return postRepository.findById(postId)
                .map(postMapper::toPostResponseWithCountLikes)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
    }

    @Override
    public List<PostResponse> findBySubCategoryId(String subCategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        List<Post> postList = postRepository.findBySubCategoryIdOrderByCreatedDateAsc(existingSubCategory.getId(), pageable);
        return postList.stream().map(postMapper::toPostResponseWithCountLikes).toList();
    }

    @Override
    public List<PostResponse> findByUserName(String userName) {
        return postRepository.findByCreatedBy(userName)
                .stream()
                .map(postMapper::toPostResponseWithCountLikes)
                .toList();
    }

//    @Override
//    public PostResponse findByTitle(String title) {
//        return iPostRepository.findByTitleContaining(title)
//                .map(iPostMapper::toPostResponseWithCountLikes)
//                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
//    }

    @Override
    public List<PostResponse> findPostsLiked(String userName) {
        return postRepository.findPostsLiked(userName)
                .stream()
                .map(postMapper::toPostResponseWithCountLikes)
                .toList();
    }

    @PostAuthorize("@IUserServiceImpl.findById(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @Override
    public PostResponse update(String id, PostRequest postRequest) {
        try {
//            MultipartFile imageFile = postRequest.getImage();
//            String base64Image = imageFile != null ? Base64.getEncoder().encodeToString(imageFile.getBytes()) : null;
            Post existingPost = postRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
//            existingPost.setImage(base64Image);
            postMapper.updatePost(existingPost, postRequest);
            return postMapper.toPostResponseWithCountLikes(postRepository.save(existingPost));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Failed to update post due to database constraint: " + e.getMessage(), e);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to update post: " + e.getMessage(), e);
        }
    }

    //    @PostAuthorize("@userServiceImpl.findByUserName(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @PostAuthorize("hasRole('ADMIN') ")
    @Override
    public void deleteByPostId(String postId) {
        try {
            Post postToDelete = postRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            postRepository.delete(postToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete post due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    @Override
    public void incrementViewCount(String postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to increase view count", e);
        }
    }
}
