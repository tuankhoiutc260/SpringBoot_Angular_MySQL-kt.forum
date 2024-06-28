package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    PostService postService;

    @GetMapping("/{id}")
    public APIResponse<PostResponse> findByID(@PathVariable String id){
        return APIResponse.<PostResponse>builder()
                .result(postService.findByID(id))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<PostResponse>> findAll(){
        return APIResponse.<List<PostResponse>>builder()
                .result(postService.findAll())
                .build();
    }

    @PostMapping
    public APIResponse<PostResponse> create(@RequestBody PostRequest postRequest){
        return APIResponse.<PostResponse>builder()
                .result(postService.create(postRequest))
                .build();
    }

    @PutMapping("/{id}")
    public APIResponse<PostResponse> update(@PathVariable String id, @RequestBody PostRequest postRequest){
        return APIResponse.<PostResponse>builder()
                .result(postService.update(id, postRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> delete(@PathVariable String id){
        postService.deleteByID(id);
        return APIResponse.<Void>builder()
                .build();
    }
}
