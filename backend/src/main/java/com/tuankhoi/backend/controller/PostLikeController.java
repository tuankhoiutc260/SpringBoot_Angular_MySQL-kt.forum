package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostLikeRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostLikeResponse;
import com.tuankhoi.backend.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Post Like Controller")
public class PostLikeController {
    PostLikeService postLikeService;

    @Operation(summary = "Toggle post like", description = "Toggle the like status of a post for the authenticated user.")
    @PostMapping("/toggle-like")
    public APIResponse<PostLikeResponse> toggleLike(@RequestBody PostLikeRequest postLikeRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return APIResponse.<PostLikeResponse>builder()
                .result(postLikeService.toggleLike(postLikeRequest, userId))
                .build();
    }

    @Operation(summary = "Check if post is liked", description = "Check if a post is liked by the authenticated user.")
    @PostMapping("/is-liked")
    public APIResponse<Boolean> isLiked(@RequestBody PostLikeRequest postLikeRequest) {
        return APIResponse.<Boolean>builder()
                .result(postLikeService.isLiked(postLikeRequest))
                .build();
    }

    @Operation(summary = "Count likes on post", description = "Count the total number of likes on a post.")
    @PostMapping("/count-likes")
    public APIResponse<Long> countLikes(@RequestBody PostLikeRequest postLikeRequest) {
        return APIResponse.<Long>builder()
                .result(postLikeService.countLikes(postLikeRequest))
                .build();
    }
}
