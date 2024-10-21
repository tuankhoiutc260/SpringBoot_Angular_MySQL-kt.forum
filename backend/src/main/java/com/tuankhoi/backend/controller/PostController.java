package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.dto.response.SubCategoryRankResponse;
import com.tuankhoi.backend.dto.response.UserRankResponse;
import com.tuankhoi.backend.enums.SearchTypeEnum;
import com.tuankhoi.backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Post Controller")
public class PostController {
    PostService postService;

    @Operation(summary = "Create new Post", description = "Create a new Post.")
    @PostMapping
    public APIResponse<PostResponse> create(@RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.create(postRequest))
                .build();
    }

    @Operation(summary = "Get Post by ID", description = "Retrieve a Post by its ID.")
    @GetMapping("/id/{postId}")
    public APIResponse<PostResponse> getById(@PathVariable String postId) {
        return APIResponse.<PostResponse>builder()
                .result(postService.getById(postId))
                .build();
    }

    @Operation(summary = "Get Posts by sub-category ID", description = "Retrieve all Posts under a specific sub-category ID.")
    @GetMapping("/sub-category/{subCategoryId}")
    public APIResponse<Page<PostResponse>> getBySubCategoryId(@PathVariable String subCategoryId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getBySubCategoryId(subCategoryId, page, size);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Get all Posts", description = "Retrieve a list of all Posts.")
    @GetMapping("")
    public APIResponse<Page<PostResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getAll(page, size);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Update Post", description = "Update a Post by its ID.")
    @PutMapping("/{postId}")
    public APIResponse<PostResponse> update(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.update(postId, postRequest))
                .build();
    }

    @Operation(summary = "Delete Post", description = "Delete a Post by its ID.")
    @DeleteMapping("/{postId}")
    public APIResponse<Void> deleteById(@PathVariable String postId) {
        postService.deleteById(postId);
        return APIResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Search Posts by sub-category", description = "Search Posts based on query and sub-category.")
    @GetMapping("/searchBySubCategoryId")
    public APIResponse<Page<PostResponse>> searchBySubCategoryId(@RequestParam String query,
                                                                 @RequestParam String subCategoryId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "BOTH") SearchTypeEnum searchTypeEnum) {
        Page<PostResponse> postResponsePage = postService.searchBySubCategoryId(query, subCategoryId, page, size, searchTypeEnum);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Search Posts with Sub Category Id", description = "Search Posts based on criteria.")
    @GetMapping("/search")
    public APIResponse<Page<PostResponse>> search(@RequestParam String query,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "BOTH") SearchTypeEnum searchTypeEnum) {
        Page<PostResponse> postResponsePage = postService.search(query, page, size, searchTypeEnum);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Increment Post view count", description = "Increment the view count of a Post by its ID.")
    @PutMapping("/{postId}/views")
    public APIResponse<Void> incrementViewCount(@PathVariable String postId) {
        postService.incrementViewCount(postId);
        return APIResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Get top 3 Posts by Likes", description = "Retrieve top 3 most liked Posts.")
    @GetMapping("/top-3")
    public APIResponse<List<PostResponse>> getTop3OrderByLikesDesc() {
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.getTop3OrderByLikesDesc())
                .build();
    }

    @Operation(summary = "Get Posts Liked by User", description = "Retrieve a paginated list of posts liked by a specific user.")
    @GetMapping("/posts-liked/{userId}")
    public APIResponse<Page<PostResponse>> getPostsLiked(@PathVariable String userId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getPostsLiked(userId, page, size);

        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Get Posts Liked by Subcategory", description = "Retrieve a paginated list of posts liked by a specific user within a subcategory.")
    @GetMapping("/posts-liked/{userId}/sub-category/{subCategoryId}")
    public APIResponse<Page<PostResponse>> getPostsLikedBySubCategoryId(@PathVariable String userId,
                                                                        @PathVariable String subCategoryId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getPostsLikedBySubCategoryId(userId, subCategoryId, page, size);

        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Get Posts Created by User", description = "Retrieve a paginated list of posts created by a specific user.")
    @GetMapping("/posts-author/{userId}")
    public APIResponse<Page<PostResponse>> getPostsByAuthor(@PathVariable String userId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getPostsByAuthor(userId, page, size);

        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @Operation(summary = "Get 5 most recently created Posts", description = "Retrieve the 5 most recently created Posts.")
    @GetMapping("/5-most-recently-created-post")
    public APIResponse<List<PostResponse>> get5MostRecentlyCreatedPosts() {
        List<PostResponse> postResponseList = postService.get5MostRecentlyCreatedPosts();

        return APIResponse.<List<PostResponse>>builder()
                .result(postResponseList)
                .build();
    }

    @Operation(summary = "Get top 3 ranked users", description = "Retrieve the ranking information of the top 3 users with the highest rank.")
    @GetMapping("/user-rank")
    public APIResponse<List<UserRankResponse>> getTop3UserRank() {
        return APIResponse.<List<UserRankResponse>>builder()
                .result(postService.getTop3UserRank())
                .build();
    }

    @Operation(summary = "Get top 10 ranked SubCategories", description = "Retrieve the ranking information of the top 3 SubCategories with the highest rank.")
    @GetMapping("/sub-category-rank")
    public APIResponse<List<SubCategoryRankResponse>> getTheTop10MostSubCategory() {
        return APIResponse.<List<SubCategoryRankResponse>>builder()
                .result(postService.getTheTop10MostSubCategory())
                .build();
    }

    @Operation(summary = "Get 6 random Posts", description = "Retrieve the 6 random Posts.")
    @GetMapping("/random-post/{postId}")
    public APIResponse<List<PostResponse>> get6RandomPosts(@PathVariable String postId) {
        List<PostResponse> postResponseList = postService.get6RandomPosts(postId);

        return APIResponse.<List<PostResponse>>builder()
                .result(postResponseList)
                .build();
    }
}
