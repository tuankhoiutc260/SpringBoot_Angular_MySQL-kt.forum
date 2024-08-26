package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import org.springframework.data.domain.Page;

public interface CommentService {
    CommentResponse create(CommentRequest commentRequest);

    CommentResponse getById(Long commentId);

    Page<CommentResponse> getAllCommentAndReplyByPostId(String postId, int page, int size);

    Page<CommentResponse> getRepliesByCommentId(Long commentId, int page, int size);

    CommentResponse update(Long commentId, CommentRequest commentRequest);

    void deleteById(Long commentId);
}