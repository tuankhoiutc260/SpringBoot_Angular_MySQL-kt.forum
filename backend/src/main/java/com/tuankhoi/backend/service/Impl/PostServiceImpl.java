package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PostMapper;
import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.repository.PostRepository;
import com.tuankhoi.backend.repository.UserRepository;
import com.tuankhoi.backend.service.AuthenticationService;
import com.tuankhoi.backend.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    //    @PostAuthorize("@userServiceImpl.findByID(returnObject.createdBy).userName == authentication.name")
    @Override
    public PostResponse create(PostRequest postRequest) {
        try {
            Post newPost = postMapper.toPost(postRequest);
            Post savedPost = postRepository.save(newPost);
            return postMapper.toPostResponse(savedPost);
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
        return postRepository.findAll(Sort.by(Sort.Order.desc("createdDate")))
                .stream()
                .map(postMapper::toPostResponse)
                .toList();
    }

    @Override
    public PostResponse findByID(String id) {
        return postRepository.findById(id)
                .map(postMapper::toPostResponse)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
    }

    @PostAuthorize("@userServiceImpl.findByUserName(returnObject.createdBy).userName == authentication.name or hasRole('ADMIN')")
    @Override
    public PostResponse update(String id, PostRequest postRequest) {
        try {
            Post existingPost = postRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            postMapper.updatePost(existingPost, postRequest);
            return postMapper.toPostResponse(postRepository.save(existingPost));
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
    public void deleteByID(String postID) {
        try {
            Post postToDelete = postRepository.findById(postID)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            postRepository.delete(postToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete post due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete post", e);
        }
    }
}
