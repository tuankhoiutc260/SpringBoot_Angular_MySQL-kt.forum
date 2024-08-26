package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostLikeRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostLikeResponse;
import com.tuankhoi.backend.service.PostLikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostLikeController {
    PostLikeService postLikeService;

    @PostMapping("/toggle")
    public APIResponse<PostLikeResponse> toggleLike(@RequestBody PostLikeRequest postLikeRequest) {
        return APIResponse.<PostLikeResponse>builder()
                .result(postLikeService.toggleLike(postLikeRequest))
                .build();
    }

    @GetMapping("/is-liked")
    public APIResponse<Boolean> isLiked(@RequestParam String postId) {
        PostLikeRequest postLikeRequest = new PostLikeRequest(postId);
        return APIResponse.<Boolean>builder()
                .result(postLikeService.isLiked(postLikeRequest))
                .build();
    }

    @GetMapping("/count-likes")
    public APIResponse<Long> countLikes(@RequestParam String postId) {
        PostLikeRequest postLikeRequest = new PostLikeRequest(postId);
        return APIResponse.<Long>builder()
                .result(postLikeService.countLikes(postLikeRequest))
                .build();
    }
}
