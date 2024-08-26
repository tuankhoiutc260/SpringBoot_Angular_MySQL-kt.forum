package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import org.springframework.data.domain.Page;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse getById(String postId);

    Page<PostResponse> getBySubCategoryId(String subCategoryId, int page, int size);

    Page<PostResponse> getAll(int page, int size);

    PostResponse update(String postId, PostRequest postRequest);

    void deleteById(String postId);

    Page<PostResponse> search(String query, int page, int size);

    void incrementViewCount(String postId);

    void indexPost(PostDocument postDocument);

    void deletePostFromElasticsearch(String postId);

//    List<PostResponse> findBySubCategoryId(String subCategoryId, int page, int size);

//    List<PostResponse> findByUserName(String userName);

//    PostResponse findByTitle(String title);

//    List<PostResponse> findPostsLiked(String userName);

//    List<PostResponse> findTop10ByOrderByLikesDesc();
}
