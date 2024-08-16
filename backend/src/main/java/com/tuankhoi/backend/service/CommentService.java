package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse create(CommentRequest commentRequest);

    CommentResponse findByCommentId(Long commentId);

    List<CommentResponse> findAllCommentAndReplyByPostId(String postId, int page, int size);

    List<CommentResponse> findRepliesByCommentId(Long commentId, int page, int size);

    CommentResponse update(Long commentId, CommentRequest commentRequest);

    void deleteByCommentId(Long commentId);
}