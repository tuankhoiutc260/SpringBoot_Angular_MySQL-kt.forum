package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
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
    public APIResponse<List<CategoryResponse>> getAll() {
        List<CategoryResponse> allCategoriesResponse = categoryService.getAll();
        return APIResponse.<List<CategoryResponse>>builder()
                .result(allCategoriesResponse)
                .totalRecords(allCategoriesResponse.size())
                .build();
    }

    @PutMapping("/{categoryId}")
    public APIResponse<CategoryResponse> update(@PathVariable String categoryId, @RequestBody CategoryRequest categoryRequest) {
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
    public APIResponse<List<CategoryResponse>> search(@RequestParam String query) {
        List<CategoryResponse> categoriesSearched = categoryService.search(query);
        return APIResponse.<List<CategoryResponse>>builder()
                .result(categoriesSearched)
                .totalRecords(categoriesSearched.size())
                .build();
    }
}


