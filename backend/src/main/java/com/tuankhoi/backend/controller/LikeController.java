package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.service.ILikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final ILikeService ILikeService;

    public LikeController(ILikeService ILikeService) {
        this.ILikeService = ILikeService;
    }

    @PostMapping("/toggle")
    public APIResponse<LikeResponse> toggleLike(@RequestBody LikeRequest likeRequest) {
        return APIResponse.<LikeResponse>builder()
                .result(ILikeService.toggleLike(likeRequest))
                .build();
    }

    @GetMapping("/is-liked")
    public APIResponse<Boolean> isLiked(@RequestParam String postId) {
        LikeRequest likeRequest = new LikeRequest(postId);
        return APIResponse.<Boolean>builder()
                .result(ILikeService.isLiked(likeRequest))
                .build();
    }

    @GetMapping("/count-likes")
    public APIResponse<Long> countLikes(@RequestParam String postId) {
        LikeRequest likeRequest = new LikeRequest(postId);
        return APIResponse.<Long>builder()
                .result(ILikeService.countLikes(likeRequest))
                .build();
    }
}