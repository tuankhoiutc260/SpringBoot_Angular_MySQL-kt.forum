package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/id/{id}")
    public APIResponse<PostResponse> findByID(@PathVariable String id) {
        return APIResponse.<PostResponse>builder()
                .result(postService.findByID(id))
                .build();
    }

    @GetMapping("/user-post/{userName}")
    public APIResponse<List<PostResponse>> findByCreatedBy(@PathVariable String userName) {
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.findByUserName(userName))
                .build();
    }

    @GetMapping("/top10")
    public APIResponse<List<PostResponse>> findTop10ByOrderByLikesDesc() {
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.findTop10ByOrderByLikesDesc())
                .build();
    }

    @GetMapping("/posts-liked/{userName}")
    public APIResponse<List<PostResponse>> findPostsLiked(@PathVariable String userName) {
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.findPostsLiked(userName))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<PostResponse>> findAll() {
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.findAll())
                .build();
    }

    @PostMapping
    public APIResponse<PostResponse> create(@ModelAttribute PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.create(postRequest))
                .build();
    }

    @PutMapping("/{id}")
    public APIResponse<PostResponse> update(@PathVariable String id, @ModelAttribute PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.update(id, postRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> delete(@PathVariable String id) {
        postService.deleteByID(id);
        return APIResponse.<Void>builder()
                .build();
    }
}
