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

    @GetMapping("/post/{postID}")
    public APIResponse<List<CommentResponse>> getCommentListByPostID(@PathVariable String postID,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size){
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.getCommentsByPostId(postID, page, size))
                .build();
    }

    @GetMapping("/{commentID}/replies")
    public APIResponse<List<CommentResponse>> getReplyCommentListByCommentID(@PathVariable Long commentID,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size){
        return APIResponse.<List<CommentResponse>>builder()
                .result(commentService.getRepliesByCommentId(commentID, page, size))
                .build();
    }

//    @DeleteMapping("/{commentID}")
//    public APIResponse<Void> delete(@PathVariable String commentID){
//        commentService.delete(commentID);
//        return APIResponse.<Void>builder()
//                .build();
//    }
}