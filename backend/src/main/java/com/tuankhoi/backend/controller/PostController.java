package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.PostDTO;
import com.tuankhoi.backend.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> findByID(@PathVariable final UUID id) {
        return ResponseEntity.ok(postService.findByID(id));
    }

    @GetMapping("")
    public ResponseEntity<List<PostDTO>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO) {
        return new ResponseEntity<>(postService.create(postDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable UUID id, @RequestBody PostDTO postDTO) {
        PostDTO updatedPost = postService.update(id, postDTO);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deleteByID(id);
        return ResponseEntity.noContent().build();
    }
}
