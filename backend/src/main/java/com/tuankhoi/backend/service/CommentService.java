package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
//    CommentResponse create(CommentRequest commentRequest) ;
//
//    List<CommentResponse> findByPostID(String postID) ;
//
//    List<CommentResponse> findByParentID(String parentID) ;
//
//    void delete(String commentID) ;

    CommentResponse addComment(CommentRequest commentRequest);
    List<CommentResponse> getCommentsByPostId(String postId, int page, int size);
    List<CommentResponse> getRepliesByCommentId(Long commentId, int page, int size);
}