package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.PostDTO;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PostMapper;
import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.repository.PostRepository;
import com.tuankhoi.backend.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDTO> findAll() {
        final List<Post> postList = postRepository.findAll(Sort.by("id"));
        return postList.stream()
                .map(PostMapper.INSTANCE::mapToDTO)
                .toList();
    }

    @Override
    public PostDTO findByID(UUID id) {
        return postRepository.findById(id)
                .map(PostMapper.INSTANCE::mapToDTO)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
    }

    @Override
    public PostDTO create(PostDTO postDTO) {
        try {
            Post post = PostMapper.INSTANCE.mapToEntity(postDTO);
            Post savedPost = postRepository.save(post);
            return PostMapper.INSTANCE.mapToDTO(savedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
    }

    @Override
    public PostDTO update(UUID id, PostDTO postDTO) {
        try {
            Post existingPost = postRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
            PostMapper.INSTANCE.updatePostFromDTO(postDTO, existingPost);

            Post updatedPost = postRepository.save(existingPost);
            return PostMapper.INSTANCE.mapToDTO(updatedPost);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update post due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update post: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByID(UUID postID) {
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
