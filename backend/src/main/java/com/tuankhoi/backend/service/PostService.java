package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse getById(String postId);

    List<PostResponse> getBySubCategoryId(String subCategoryId, int page, int size);

    List<PostResponse> getAll();

    PostResponse update(String postId, PostRequest postRequest);

    void deleteById(String postId);

    List<PostResponse> search(String query);



    //
//    List<PostResponse> findBySubCategoryId(String subCategoryId, int page, int size);
//
//    List<PostResponse> findByUserName(String userName);
//
////    PostResponse findByTitle(String title);
//
//    List<PostResponse> findPostsLiked(String userName);
//
//
//    List<PostResponse> findTop10ByOrderByLikesDesc();
//
//
//
    void incrementViewCount(String postId);

    void indexPost(PostDocument postDocument);

}
