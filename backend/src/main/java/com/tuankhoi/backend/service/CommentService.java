package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse create(CommentRequest commentRequest);

    CommentResponse getById(Long commentId);

    List<CommentResponse> getAllCommentAndReplyByPostId(String postId, int page, int size);

    List<CommentResponse> getRepliesByCommentId(Long commentId, int page, int size);

    CommentResponse update(Long commentId, CommentRequest commentRequest);

    void deleteById(Long commentId);
}