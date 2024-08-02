package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse findById(String postId);

    List<PostResponse> findByUserName(String userName);

    List<PostResponse> findPostsLiked(String userName);

    List<PostResponse> findAll();

    List<PostResponse> findTop10ByOrderByLikesDesc();

    PostResponse update(String id, PostRequest postRequest);

    void deleteById(String id);
}
