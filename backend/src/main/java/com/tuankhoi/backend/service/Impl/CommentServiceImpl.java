package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.dto.websocket.WebSocketMessage;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.CommentMapper;
import com.tuankhoi.backend.model.entity.Comment;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.repository.Jpa.CommentRepository;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    PostRepository postRepository;
    CommentMapper commentMapper;

    SimpMessagingTemplate messagingTemplate;

    @Transactional
    @CacheEvict(value = {"comment", "comments"}, allEntries = true)
    @Caching(evict = {
            @CacheEvict(value = "comment", allEntries = true),
            @CacheEvict(value = "comments", allEntries = true),
    })
    @Override
    public CommentResponse create(CommentRequest commentRequest) {
        Post existingPost = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

        try {
            Comment newComment = commentMapper.toComment(commentRequest);
            newComment.setPost(existingPost);

            if (commentRequest.getParentCommentId() != null) {
                Comment parentComment = commentRepository.findById(commentRequest.getParentCommentId())
                        .orElseThrow(() -> new AppException(ErrorCode.PARENT_COMMENT_NOTFOUND));
                newComment.setParentComment(parentComment);
            } else {
                newComment.setParentComment(null);
            }

            Comment savedComment = commentRepository.save(newComment);
            CommentResponse commentResponse = commentMapper.toCommentResponse(savedComment);

            WebSocketMessage addCommentMessage = WebSocketMessage.builder()
                    .type("NEW_COMMENT")
                    .payload(commentResponse)
                    .build();
            messagingTemplate.convertAndSend("/topic/comments/" + commentResponse.getPostId(), addCommentMessage);
            log.warn(addCommentMessage.toString());

            return commentResponse;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create Comment due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create Comment", e);
        }
    }

    @Override
    @Cacheable(value = "comment", key = "#commentId", unless = "#result == null")
    public CommentResponse getById(Long commentId) {
        return commentRepository.findById(commentId)
                .map(commentMapper::toCommentResponse)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Cacheable(value = "comments", key = "'all:postId:' + #postId + ',page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<CommentResponse> getAllCommentAndReplyByPostId(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Comment> commentPage = commentRepository.findAllByPostIdOrderByCreatedDateAsc(postId, pageable);
        return commentPage.map(commentMapper::toCommentResponse);
    }

    @Cacheable(value = "commentReplies", key = "'commentId:' + #commentId + ',page: ' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<CommentResponse> getRepliesByCommentId(Long commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));
        Page<Comment> commentPage = commentRepository.findRepliesByCommentId(commentId, pageable);
        return commentPage.map(commentMapper::toCommentResponse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "comment", key = "#commentId"),
            @CacheEvict(value = "comments", allEntries = true),
    })
    @Override
    public CommentResponse update(Long commentId, CommentRequest commentRequest) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        try {
            existingComment.setContent(commentRequest.getContent());
            Comment updatedComment = commentRepository.save(existingComment);
            CommentResponse commentResponse = commentMapper.toCommentResponse(updatedComment);

            WebSocketMessage updateCommentMessage = WebSocketMessage.builder()
                    .type("UPDATE_COMMENT")
                    .payload(commentResponse)
                    .build();
            messagingTemplate.convertAndSend("/topic/comments/" + existingComment.getPost().getId() + "/update", updateCommentMessage);

            return commentResponse;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Failed to Update Comment due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update Comment", e);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "comment", key = "#commentId"),
            @CacheEvict(value = "comments", allEntries = true),
    })
    @Override
    public void deleteById(Long commentId) {
        Comment commentToDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        try {
            String postId = commentToDelete.getPost().getId();
            commentRepository.deleteById(commentId);

            WebSocketMessage deleteCommentMessage = WebSocketMessage.builder()
                    .type("DELETE_COMMENT")
                    .payload(commentId)
                    .build();
            messagingTemplate.convertAndSend("/topic/comments/" + postId + "/delete", deleteCommentMessage);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete Comment due to database constraint", e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Comment", e);
        }
    }
}
