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
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public APIResponse<CommentResponse> create(@RequestBody CommentRequest commentRequest){
        return APIResponse.<CommentResponse>builder()
                .result(commentService.addComment(commentRequest))
                .build();
    }

    @GetMapping("/post/{postId}")
    public APIResponse<List<CommentResponse>> getCommentListByPostId(@PathVariable String postId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size){
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsByPostId(postId, page, size))
                .build();
    }

    @GetMapping("/{commentId}/replies")
    public APIResponse<List<CommentResponse>> getRepliesByCommentId(@PathVariable Long commentId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.getRepliesByCommentId(commentId, page, size))
                .build();
    }

//    @GetMapping("/{commentId}/all-replies")
//    public APIResponse<List<CommentResponse>> getAllRepliesForComment(@PathVariable Long commentId,
//                                                                      @RequestParam(defaultValue = "0") int page,
//                                                                      @RequestParam(defaultValue = "5") int size) {
//        return APIResponse.<List<CommentResponse>>builder()
//                .result(commentService.getAllReplyCommentsByCommentId(commentId, page, size))
//                .build();
//    }

    @PutMapping("/{commentId}")
    public APIResponse<CommentResponse> update(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.updateComment(commentId, commentRequest))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public APIResponse<Void> delete(@PathVariable Long commentId){
        commentService.deleteComment(commentId);
        return APIResponse.<Void>builder()
                .build();
    }
}