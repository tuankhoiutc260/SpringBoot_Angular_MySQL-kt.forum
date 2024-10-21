package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.dto.response.SubCategoryRankResponse;
import com.tuankhoi.backend.dto.response.UserRankResponse;
import com.tuankhoi.backend.enums.SearchTypeEnum;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse getById(String postId);

    Page<PostResponse> getBySubCategoryId(String subCategoryId, int page, int size);

    Page<PostResponse> getAll(int page, int size);

    PostResponse update(String postId, PostRequest postRequest);

    void deleteById(String postId);

    Page<PostResponse> search(String query, int page, int size, SearchTypeEnum searchTypeEnum);

    Page<PostResponse> searchBySubCategoryId(String query, String subCategoryId, int page, int size, SearchTypeEnum searchTypeEnum);

    void incrementViewCount(String postId);

    void indexPost(PostDocument postDocument);

    void deletePostFromElasticsearch(String postId);

    List<PostResponse> getTop3OrderByLikesDesc();

    Page<PostResponse> getPostsLiked(String userId, int page, int size);

    Page<PostResponse> getPostsLikedBySubCategoryId(String userId, String subCategoryId, int page, int size);

    Page<PostResponse> getPostsByAuthor(String userId, int page, int size);

    List<PostResponse> get5MostRecentlyCreatedPosts();

    List<UserRankResponse> getTop3UserRank();

    List<SubCategoryRankResponse> getTheTop10MostSubCategory();

    List<PostResponse> get6RandomPosts(String postId);
}
