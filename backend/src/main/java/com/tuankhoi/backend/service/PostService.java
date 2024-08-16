package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse findById(String postId);

    List<PostResponse> findBySubCategoryId(String subCategoryId, int page, int size);

    List<PostResponse> findByUserName(String userName);

    PostResponse findByTitle(String title);

    List<PostResponse> findPostsLiked(String userName);

    List<PostResponse> findAll();

    List<PostResponse> findTop10ByOrderByLikesDesc();

    PostResponse update(String id, PostRequest postRequest);

    void deleteByPostId(String postId);
}
