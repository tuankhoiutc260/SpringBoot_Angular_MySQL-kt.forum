package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.service.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle")
    public APIResponse<LikeResponse> toggleLike(@RequestBody LikeRequest likeRequest) {
        return APIResponse.<LikeResponse>builder()
                .result(likeService.toggleLike(likeRequest))
                .build();
    }

    @GetMapping("/is-liked")
    public APIResponse<Boolean> isLiked(@RequestParam String postId) {
        LikeRequest likeRequest = new LikeRequest(postId);
        return APIResponse.<Boolean>builder()
                .result(likeService.isLiked(likeRequest))
                .build();
    }

    @GetMapping("/count-likes")
    public APIResponse<Long> countLikes(@RequestParam String postId) {
        LikeRequest likeRequest = new LikeRequest(postId);
        return APIResponse.<Long>builder()
                .result(likeService.countLikes(likeRequest))
                .build();
    }
}