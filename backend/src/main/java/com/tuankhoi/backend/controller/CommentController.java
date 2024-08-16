package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class    CommentController {
    private final CommentService commentService;

    @PostMapping
    public APIResponse<CommentResponse> create(@RequestBody CommentRequest commentRequest){
        return APIResponse.<CommentResponse>builder()
                .result(commentService.create(commentRequest))
                .build();
    }

    @GetMapping("/id/{commentId}")
    public APIResponse<CommentResponse> findByCommentId(@PathVariable Long commentId){
        return APIResponse.<CommentResponse>builder()
                .result(commentService.findByCommentId(commentId))
                .build();
    }

    @GetMapping("/post/{postId}")
    public APIResponse<List<CommentResponse>> findAllCommentAndReplyByPostId(@PathVariable String postId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size){
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.findAllCommentAndReplyByPostId(postId, page, size))
                .build();
    }

    @GetMapping("/{commentId}/replies")
    public APIResponse<List<CommentResponse>> findRepliesByCommentId(@PathVariable Long commentId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.findRepliesByCommentId(commentId, page, size))
                .build();
    }

    @PutMapping("/{commentId}")
    public APIResponse<CommentResponse> update(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.update(commentId, commentRequest))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public APIResponse<Void> deleteByCommentId(@PathVariable Long commentId){
        commentService.deleteByCommentId(commentId);
        return APIResponse.<Void>builder()
                .build();
    }
}