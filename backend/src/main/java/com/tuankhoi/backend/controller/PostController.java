package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping
    public APIResponse<PostResponse> create(@RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.create(postRequest))
                .build();
    }

    @GetMapping("/id/{postId}")
    public APIResponse<PostResponse> getById(@PathVariable String postId) {
        return APIResponse.<PostResponse>builder()
                .result(postService.getById(postId))
                .build();
    }

    @GetMapping("/sub-category/{subCategoryId}")
    public APIResponse<Page<PostResponse>> getBySubCategoryId(@PathVariable String subCategoryId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getBySubCategoryId(subCategoryId, page, size);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @GetMapping("")
    public APIResponse<Page<PostResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.getAll(page, size);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @PutMapping("/{postId}")
    public APIResponse<PostResponse> update(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.update(postId, postRequest))
                .build();
    }

    @DeleteMapping("/{postId}")
    public APIResponse<Void> deleteById(@PathVariable String postId) {
        postService.deleteById(postId);
        return APIResponse.<Void>builder()
                .build();
    }

    @GetMapping("/search")
    public APIResponse<Page<PostResponse>> search(@RequestParam String query,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> postResponsePage = postService.search(query, page, size);
        return APIResponse.<Page<PostResponse>>builder()
                .result(postResponsePage)
                .build();
    }

    @PutMapping("/{postId}/view")
    public APIResponse<Void> incrementViewCount(@PathVariable String postId) {
        postService.incrementViewCount(postId);
        return APIResponse.<Void>builder()
                .build();
    }

//    @GetMapping("/user-post/{userName}")
//    public APIResponse<Page<PostResponse>> findByCreatedBy(@PathVariable String userName) {
//        Page<PostResponse> postResponsePage = PostService.findByUserName(userName);
//        return APIResponse.<Page<PostResponse>>builder()
//                .result(postResultList)
//                .build();
//    }

//    @GetMapping("/title/{title}")
//    public APIResponse<PostResponse> findByTitle(@PathVariable String title) {
//        return APIResponse.<PostResponse>builder()
//                .result(IPostService.findByTitle(title))
//                .build();
//    }

//    @GetMapping("/top10")
//    public APIResponse<List<PostResponse>> findTop10ByOrderByLikesDesc() {
//        return APIResponse.<List<PostResponse>>builder()
//                .result(PostService.findTop10ByOrderByLikesDesc())
//                .build();
//    }

//    @GetMapping("/posts-liked/{userName}")
//    public APIResponse<List<PostResponse>> findPostsLiked(@PathVariable String userName) {
//        return APIResponse.<List<PostResponse>>builder()
//                .result(PostService.findPostsLiked(userName))
//                .build();
//    }
}
