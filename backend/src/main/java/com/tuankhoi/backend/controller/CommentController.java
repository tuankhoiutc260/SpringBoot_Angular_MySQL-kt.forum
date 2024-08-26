package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public APIResponse<CommentResponse> create(@RequestBody CommentRequest commentRequest){
        return APIResponse.<CommentResponse>builder()
                .result(commentService.create(commentRequest))
                .build();
    }

    @GetMapping("/id/{commentId}")
    public APIResponse<CommentResponse> getById(@PathVariable Long commentId){
        return APIResponse.<CommentResponse>builder()
                .result(commentService.getById(commentId))
                .build();
    }

    @GetMapping("/post/{postId}")
    public APIResponse<Page<CommentResponse>> getAllCommentAndReplyByPostId(@PathVariable String postId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size){
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentAndReplyByPostId(postId, page, size);
        return APIResponse.<Page<CommentResponse>>builder()
                .result(commentResponsePage)
                .build();
    }

    @GetMapping("/{commentId}/replies")
    public APIResponse<Page<CommentResponse>> getRepliesByCommentId(@PathVariable Long commentId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size) {
        Page<CommentResponse> commentResponsePage = commentService.getRepliesByCommentId(commentId, page, size);
        return APIResponse.<Page<CommentResponse>>builder()
                .result(commentResponsePage)
                .build();
    }

    @PutMapping("/{commentId}")
    public APIResponse<CommentResponse> update(@PathVariable Long commentId,
                                               @RequestBody CommentRequest commentRequest) {
        return APIResponse.<CommentResponse>builder()
                .result(commentService.update(commentId, commentRequest))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public APIResponse<Void> deleteById(@PathVariable Long commentId){
        commentService.deleteById(commentId);
        return APIResponse.<Void>builder()
                .build();
    }
}
