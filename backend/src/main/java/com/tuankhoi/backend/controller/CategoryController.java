package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public APIResponse<CategoryResponse> create(@RequestBody CategoryRequest categoryRequest) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.create(categoryRequest))
                .build();
    }

    @GetMapping("/{categoryId}")
    public APIResponse<CategoryResponse> getById(@PathVariable String categoryId) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.getById(categoryId))
                .build();
    }

    @GetMapping("")
    public APIResponse<Page<CategoryResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<CategoryResponse> categoryResponsePage = categoryService.getAll(page, size);
        return APIResponse.<Page<CategoryResponse>>builder()
                .result(categoryResponsePage)
                .build();
    }

    @PutMapping("/{categoryId}")
    public APIResponse<CategoryResponse> update(@PathVariable String categoryId,
                                                @RequestBody CategoryRequest categoryRequest) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.update(categoryId, categoryRequest))
                .build();
    }

    @DeleteMapping("/{categoryId}")
    public APIResponse<Void> deleteById(@PathVariable String categoryId) {
        categoryService.deleteById(categoryId);
        return APIResponse.<Void>builder()
                .build();
    }

    @GetMapping("/search")
    public APIResponse<Page<CategoryResponse>> search(@RequestParam String query,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<CategoryResponse> categoryResponsePage = categoryService.search(query, page, size);
        return APIResponse.<Page<CategoryResponse>>builder()
                .result(categoryResponsePage)
                .build();
    }
}
