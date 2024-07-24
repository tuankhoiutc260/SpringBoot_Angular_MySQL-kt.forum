package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse create(CommentRequest commentRequest) ;

    List<CommentResponse> findByPostID(String postID) ;

    void delete(String commentID) ;
}