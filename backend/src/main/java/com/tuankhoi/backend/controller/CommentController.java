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
                .result(commentService.create(commentRequest))
                .build();
    }

    @GetMapping("/post/{postID}")
    public APIResponse<List<CommentResponse>> findByPostID(@PathVariable String postID){
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.findByPostID(postID))
                .build();
    }

    @DeleteMapping("/{commentID}")
    public APIResponse<Void> delete(@PathVariable String commentID){
        commentService.delete(commentID);
        return APIResponse.<Void>builder()
                .build();
    }
}