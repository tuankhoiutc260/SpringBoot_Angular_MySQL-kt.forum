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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Post Like Controller")
public class PostLikeController {
    PostLikeService postLikeService;

    @Operation(summary = "Toggle post like", description = "Toggle the like status of a post for the authenticated user.")
    @PostMapping("/toggle")
    public APIResponse<PostLikeResponse> toggleLike(@RequestBody PostLikeRequest postLikeRequest) {
        return APIResponse.<PostLikeResponse>builder()
                .result(postLikeService.toggleLike(postLikeRequest))
                .build();
    }

    @Operation(summary = "Check if post is liked", description = "Check if a post is liked by the authenticated user.")
    @GetMapping("/is-liked")
    public APIResponse<Boolean> isLiked(@RequestParam String postId) {
        PostLikeRequest postLikeRequest = new PostLikeRequest(postId);
        return APIResponse.<Boolean>builder()
                .result(postLikeService.isLiked(postLikeRequest))
                .build();
    }

    @Operation(summary = "Count likes on post", description = "Count the total number of likes on a post.")
    @GetMapping("/count-likes")
    public APIResponse<Long> countLikes(@RequestParam String postId) {
        PostLikeRequest postLikeRequest = new PostLikeRequest(postId);
        return APIResponse.<Long>builder()
                .result(postLikeService.countLikes(postLikeRequest))
                .build();
    }
}
