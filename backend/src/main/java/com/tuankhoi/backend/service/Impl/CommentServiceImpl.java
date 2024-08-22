package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.dto.websocket.WebSocketMessage;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.ICommentMapper;
import com.tuankhoi.backend.model.entity.Comment;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.repository.Jpa.ICommentRepository;
import com.tuankhoi.backend.repository.Jpa.IPostRepository;
import com.tuankhoi.backend.service.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements ICommentService {
    private final ICommentRepository ICommentRepository;
    private final IPostRepository IPostRepository;
    private final ICommentMapper ICommentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public CommentResponse create(CommentRequest commentRequest) {
        Post existingPost = IPostRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

        Comment newComment = ICommentMapper.toEntity(commentRequest);
        newComment.setPost(existingPost);

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = ICommentRepository.findById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_COMMENT_NOTFOUND));
            newComment.setParentComment(parentComment);
        } else {
            newComment.setParentComment(null);
        }
        Comment savedComment = ICommentRepository.save(newComment);
        CommentResponse commentResponse = ICommentMapper.toResponse(savedComment);

        WebSocketMessage addCommentMessage = WebSocketMessage.builder()
                .type("NEW_COMMENT")
                .payload(commentResponse)
                .build();
        messagingTemplate.convertAndSend("/topic/comments/" + commentResponse.getPostId(), addCommentMessage);
        return commentResponse;
    }

    @Override
    public CommentResponse findByCommentId(Long commentId) {
        return ICommentRepository.findById(commentId)
                .map(ICommentMapper:: toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Override
    public List<CommentResponse> findAllCommentAndReplyByPostId(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Comment> commentList = ICommentRepository.findAllByPostIdOrderByCreatedDateDesc(postId, pageable);
        return commentList.stream().map(ICommentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> findRepliesByCommentId(Long commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Comment> commentList = ICommentRepository.findRepliesByCommentId(commentId, pageable);
        return commentList.stream().map(ICommentMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CommentResponse update(Long commentId, CommentRequest commentRequest) {
        try {
            Comment existingComment = ICommentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            existingComment.setContent(commentRequest.getContent());
            Comment updatedComment = ICommentRepository.save(existingComment);
            CommentResponse commentResponse = ICommentMapper.toResponse(updatedComment);
//            messagingTemplate.convertAndSend("/topic/comments/" + existingComment.getPost().getId() + "/update", commentResponse);
            WebSocketMessage updateCommentMessage = WebSocketMessage.builder()
                    .type("UPDATE_COMMENT")
                    .payload(commentResponse)
                    .build();
            messagingTemplate.convertAndSend("/topic/comments/" + existingComment.getPost().getId() + "/update", updateCommentMessage);
            return commentResponse;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION, "Failed to update post due to database constraint: " + e.getMessage(), e);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to update comment: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByCommentId(Long commentId) {
        try {
            Comment commentToDelete = ICommentRepository.findById(commentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
            String postId = commentToDelete.getPost().getId();
            ICommentRepository.delete(commentToDelete);
//            messagingTemplate.convertAndSend("/topic/comments/" + postId + "/delete", commentId);
            WebSocketMessage deleteCommentMessage = WebSocketMessage.builder()
                    .type("DELETE_COMMENT")
                    .payload(commentId)
                    .build();
            messagingTemplate.convertAndSend("/topic/comments/" + postId + "/delete", deleteCommentMessage);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete comment due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }
}


