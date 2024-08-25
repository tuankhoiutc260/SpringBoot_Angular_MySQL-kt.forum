package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;
    PostRepository postRepository;

    @PostMapping
    public APIResponse<PostResponse> create(@RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.create(postRequest))
                .build();
    }

    @GetMapping("/id/{id}")
    public APIResponse<PostResponse> getById(@PathVariable String id) {
        return APIResponse.<PostResponse>builder()
                .result(postService.getById(id))
                .build();
    }

    @GetMapping("/sub-category/{subCategoryId}")
    public APIResponse<List<PostResponse>> getBySubCategoryId(@PathVariable String subCategoryId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        List<PostResponse> postResponseList = postService.getBySubCategoryId(subCategoryId, page, size);
        return APIResponse.<List<PostResponse>>builder()
                .result(postResponseList)
                .totalRecords(postResponseList.size())
                .build();
    }

    @GetMapping("")
    public APIResponse<List<PostResponse>> getAll() {
        List<PostResponse> postResponseList = postService.getAll();
        return APIResponse.<List<PostResponse>>builder()
                .result(postResponseList)
                .totalRecords(postResponseList.size() )
                .build();
    }

    @PutMapping("/{postId}")
    public APIResponse<PostResponse> update(@PathVariable String postId, @RequestBody PostRequest postRequest) {
        return APIResponse.<PostResponse>builder()
                .result(postService.update(postId, postRequest))
                .build();
    }

    @DeleteMapping("/{postId}")
    public APIResponse<Void> deleteById(@PathVariable String postId) {
        postService.deleteById(postId);
        return APIResponse.<Void>builder()
                .build();
    }

    @GetMapping("/search")
    public APIResponse<List<PostResponse>> search(@RequestParam String query) {
        List<PostResponse> postResponseList = postService.search(query);
        return APIResponse.<List<PostResponse>>builder()
                .result(postResponseList)
                .totalRecords(postResponseList.size())
                .build();
    }
//
//    @GetMapping("/user-post/{userName}")
//    public APIResponse<List<PostResponse>> findByCreatedBy(@PathVariable String userName) {
//        List<PostResponse> postResultList = PostService.findByUserName(userName);
//        return APIResponse.<List<PostResponse>>builder()
//                .result(postResultList)
//                .totalRecords(postResultList.size())
//                .build();
//    }
//
////    @GetMapping("/title/{title}")
////    public APIResponse<PostResponse> findByTitle(@PathVariable String title) {
////        return APIResponse.<PostResponse>builder()
////                .result(IPostService.findByTitle(title))
////                .build();
////    }
//
//    @GetMapping("/top10")
//    public APIResponse<List<PostResponse>> findTop10ByOrderByLikesDesc() {
//        return APIResponse.<List<PostResponse>>builder()
//                .result(PostService.findTop10ByOrderByLikesDesc())
//                .build();
//    }
//
//    @GetMapping("/posts-liked/{userName}")
//    public APIResponse<List<PostResponse>> findPostsLiked(@PathVariable String userName) {
//        return APIResponse.<List<PostResponse>>builder()
//                .result(PostService.findPostsLiked(userName))
//                .build();
//    }
//

//

//

//

//
//    @PutMapping("/{postId}/view")
//    public APIResponse<Void> incrementViewCount(@PathVariable String postId) {
//        PostService.incrementViewCount(postId);
//        return APIResponse.<Void>builder()
//                .build();
//    }
}
