package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.IPostMapper;
import com.tuankhoi.backend.model.entity.Post;
//import com.tuankhoi.backend.repository.ElasticSearch.IElasticsearchPostRepository;
import com.tuankhoi.backend.repository.Jpa.IPostRepository;
import com.tuankhoi.backend.repository.Jpa.ISubCategoryRepository;
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
    private final ISubCategoryRepository iSubCategoryRepository;
    private final IPostRepository iPostRepository;
    private final IPostMapper iPostMapper;

//    private final IElasticsearchPostRepository iElasticsearchPostRepository;

    //    @PostAuthorize("@userServiceImpl.findById(returnObject.createdBy).userName == authentication.name")
    @Override
    public PostResponse create(PostRequest postRequest) {
        try {
            log.warn(postRequest.toString());
            Post newPost = iPostMapper.toPost(postRequest);
            Post savedPost = iPostRepository.save(newPost);
            return iPostMapper.toPostResponseWithCountLikes(savedPost);
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


        return iPostRepository.findAll(Sort.by(Sort.Order.asc("createdDate")))
                .stream()
                .map(iPostMapper::toPostResponseWithCountLikes)
                .toList();
    }

    @Override
    public List<PostResponse> findTop10ByOrderByLikesDesc() {
        return iPostRepository.findTop10ByOrderByLikesDesc().stream().map(iPostMapper::toPostResponseWithCountLikes).toList();
    }

    @Override
    public PostResponse findById(String postId) {
        return iPostRepository.findById(postId)
                .map(iPostMapper::toPostResponseWithCountLikes)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
    }

    @Override
    public List<PostResponse> findBySubCategoryId(String subCategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        SubCategory existingSubCategory = iSubCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        List<Post> postList = iPostRepository.findBySubCategoryIdOrderByCreatedDateAsc(existingSubCategory.getId(), pageable);
        return postList.stream().map(iPostMapper::toPostResponseWithCountLikes).toList();
    }

    @Override
    public List<PostResponse> findByUserName(String userName) {
        return iPostRepository.findByCreatedBy(userName)
                .stream()
                .map(iPostMapper::toPostResponseWithCountLikes)
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
        return iPostRepository.findPostsLiked(userName)
                .stream()
                .map(iPostMapper::toPostResponseWithCountLikes)
                .toList();
    }

    @PostAuthorize("@IUserServiceImpl.findById(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @Override
    public PostResponse update(String id, PostRequest postRequest) {
        try {
//            MultipartFile imageFile = postRequest.getImage();
//            String base64Image = imageFile != null ? Base64.getEncoder().encodeToString(imageFile.getBytes()) : null;
            Post existingPost = iPostRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
//            existingPost.setImage(base64Image);
            iPostMapper.updatePost(existingPost, postRequest);
            return iPostMapper.toPostResponseWithCountLikes(iPostRepository.save(existingPost));
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
            Post postToDelete = iPostRepository.findById(postId)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            iPostRepository.delete(postToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete post due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    @Override
    public void incrementViewCount(String postId) {
        try {
            Post post = iPostRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            post.setViewCount(post.getViewCount() + 1);
            iPostRepository.save(post);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to increase view count", e);
        }
    }
}
