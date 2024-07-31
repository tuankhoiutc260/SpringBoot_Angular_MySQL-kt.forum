package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        Post existingPost = postRepository.findById(commentRequest.getPostID())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

        Comment newComment = commentMapper.toEntity(commentRequest);
        newComment.setPost(existingPost);

        if (commentRequest.getParentCommentID() != null) {
            Comment parentComment = commentRepository.findById(commentRequest.getParentCommentID())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_COMMENT_NOTFOUND));
            newComment.setParentComment(parentComment);
        } else {
            newComment.setParentComment(null);
        }
        Comment savedComment = commentRepository.save(newComment);
        CommentResponse commentResponse = commentMapper.toResponse(savedComment);
        messagingTemplate.convertAndSend("/topic/comments/" + existingPost.getId(), commentResponse);
        return commentResponse;
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(String postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Comment> commentList = commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedDateDesc(postId, pageable);

        return commentList.stream().map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getAllReplyCommentsByCommentId(Long commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Comment> commentList = commentRepository.findAllReplyCommentsByCommentId(commentId, pageable);
        return commentList.stream().map(commentMapper::toResponse).collect(Collectors.toList());
    }
}


