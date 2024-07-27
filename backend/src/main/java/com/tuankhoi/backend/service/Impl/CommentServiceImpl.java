package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.dto.websocket.WebSocketMessage;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.CommentMapper;
import com.tuankhoi.backend.model.Comment;
import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.repository.CommentRepository;
import com.tuankhoi.backend.repository.PostRepository;
import com.tuankhoi.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public CommentResponse create(CommentRequest commentRequest) {
        try {
//            Comment existingComment = commentRepository.findById(commentRequest.getPostID()).o
            Post post = postRepository.findById(commentRequest.getPostID())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_POST_ID_NOTBLANK));

            Comment newComment = commentMapper.toEntity(commentRequest);
            newComment.setPost(post);

            if (commentRequest.getParentID() != null) {
                Comment parentComment = commentRepository.findById(commentRequest.getParentID())
                        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_PARENT_ID_NOTBLANK));
                newComment.setParent(parentComment);
            }
            else{
                newComment.setParent(null);
            }
            Comment savedComment = commentRepository.save(newComment);

            CommentResponse response = commentMapper.toResponse(savedComment);

            // Send WebSocket message
            messagingTemplate.convertAndSend("/topic/comments/" + post.getId(),
                    new WebSocketMessage("NEW_COMMENT", response));

            return response;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Failed to post comment due to database constraint: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to post comment due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to post comment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to post comment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CommentResponse> findByPostID(String postID) {
        return commentRepository.findByPostIdOrderByCreatedDateDesc(postID).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> findByParentID(String parentID) {
        return commentRepository.findByParentId(parentID).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(String commentID) {
        Comment comment = commentRepository.findById(commentID)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        String postId = comment.getPost().getId();
        commentRepository.delete(comment);
        // Send WebSocket message
        messagingTemplate.convertAndSend("/topic/comments/" + postId,
                new WebSocketMessage("DELETE_COMMENT", commentID));
    }
}
