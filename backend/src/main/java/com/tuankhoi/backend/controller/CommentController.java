package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comment Controller")
public class CommentController {
    CommentService commentService;

    @Operation(summary = "Create new comment", description = "Create a new comment.")
    @PostMapping
    public APIResponse<CommentResponse> create(@RequestBody CommentRequest commentRequest) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.create(commentRequest))
                .build();
    }

    @Operation(summary = "Get comment by ID", description = "Retrieve a comment by its ID.")
    @GetMapping("/id/{commentId}")
    public APIResponse<CommentResponse> getById(@PathVariable Long commentId) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.getById(commentId))
                .build();
    }

    @Operation(summary = "Get comments by post ID", description = "Retrieve all comments under a specific post ID.")
    @GetMapping("/post/{postId}")
    public APIResponse<Page<CommentResponse>> getAllCommentAndReplyByPostId(@PathVariable String postId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentAndReplyByPostId(postId, page, size);
        return APIResponse.<Page<CommentResponse>>builder()
                .result(commentResponsePage)
                .build();
    }

    @Operation(summary = "Get replies to a comment", description = "Retrieve all replies to a specific comment ID.")
    @GetMapping("/{commentId}/replies")
    public APIResponse<Page<CommentResponse>> getRepliesByCommentId(@PathVariable Long commentId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size) {
        Page<CommentResponse> commentResponsePage = commentService.getRepliesByCommentId(commentId, page, size);
        return APIResponse.<Page<CommentResponse>>builder()
                .result(commentResponsePage)
                .build();
    }

    @Operation(summary = "Update comment", description = "Update a comment by its ID.")
    @PutMapping("/{commentId}")
    public APIResponse<CommentResponse> update(@PathVariable Long commentId,
                                               @RequestBody CommentRequest commentRequest) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.update(commentId, commentRequest))
                .build();
    }

    @Operation(summary = "Delete comment", description = "Delete a comment by its ID.")
    @DeleteMapping("/{commentId}")
    public APIResponse<Void> deleteById(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
        return APIResponse.<Void>builder()
                .build();
    }
}
