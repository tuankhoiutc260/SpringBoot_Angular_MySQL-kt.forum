package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.repository.Jpa.IPostRepository;
import com.tuankhoi.backend.service.IPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final IPostService IPostService;
    private final IPostRepository IPostRepository;

    public PostController(IPostService IPostService, IPostRepository IPostRepository) {
        this.IPostService = IPostService;
        this.IPostRepository = IPostRepository;
    }

    @GetMapping("/id/{id}")
    public APIResponse<PostResponse> findById(@PathVariable String id) {
        return APIResponse.<PostResponse>builder()
                .result(IPostService.findById(id))
                .build();
    }

    @GetMapping("/sub-category/{subCategoryId}")
    public APIResponse<List<PostResponse>> findBySubCategory(@PathVariable String subCategoryId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {

        return APIResponse.<List<PostResponse>>builder()
                .result(IPostService.findBySubCategoryId(subCategoryId, page, size))
                .totalRecords(IPostRepository.countBySubCategoryId(subCategoryId))
                .build();
    }

    @GetMapping("/user-post/{userName}")
    public APIResponse<List<PostResponse>> findByCreatedBy(@PathVariable String userName) {
        return APIResponse.<List<PostResponse>>builder()
                .result(IPostService.findByUserName(userName))
                .totalRecords(IPostRepository.countByCreatedBy(userName))
                .build();
    }

//    @GetMapping("/title/{title}")
//    public APIResponse<PostResponse> findByTitle(@PathVariable String title) {
//        return APIResponse.<PostResponse>builder()
//                .result(IPostService.findByTitle(title))
//                .build();
//    }

    @GetMapping("/top10")
    public APIResponse<List<PostResponse>> findTop10ByOrderByLikesDesc() {
        return APIResponse.<List<PostResponse>>builder()
                .result(IPostService.findTop10ByOrderByLikesDesc())
                .build();
    }

    @GetMapping("/posts-liked/{userName}")
    public APIResponse<List<PostResponse>> findPostsLiked(@PathVariable String userName) {
        return APIResponse.<List<PostResponse>>builder()
                .result(IPostService.findPostsLiked(userName))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<PostResponse>> findAll() {
        return APIResponse.<List<PostResponse>>builder()
                .result(IPostService.findAll())
                .build();
    }

    @PostMapping
    public APIResponse<PostResponse> create(@ModelAttribute PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(IPostService.create(postRequest))
                .build();
    }

    @PutMapping("/{postId}")
    public APIResponse<PostResponse> update(@PathVariable String postId, @ModelAttribute PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(IPostService.update(postId, postRequest))
                .build();
    }

    @DeleteMapping("/{postId}")
    public APIResponse<Void> delete(@PathVariable String postId) {
        IPostService.deleteByPostId(postId);
        return APIResponse.<Void>builder()
                .build();
    }

    @PutMapping("/{postId}/view")
    public APIResponse<Void> incrementViewCount(@PathVariable String postId) {
        IPostService.incrementViewCount(postId);
        return APIResponse.<Void>builder()
                .build();
    }
}
