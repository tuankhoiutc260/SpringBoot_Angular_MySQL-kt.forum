package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Category Controller")
public class CategoryController {
    CategoryService categoryService;

    @Operation(summary = "Create new Category", description = "Create a new Category.")
    @PostMapping
    public APIResponse<CategoryResponse> create(@RequestBody CategoryRequest categoryRequest) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.create(categoryRequest))
                .build();
    }

    @Operation(summary = "Get Category by ID", description = "Retrieve a Category by its ID.")
    @GetMapping("/category/{categoryId}")
    public APIResponse<CategoryResponse> getById(@PathVariable String categoryId) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.getById(categoryId))
                .build();
    }

    @Operation(summary = "Get Category by SubCategory ID", description = "Retrieve a Category by SubCategory ID.")
    @GetMapping("/sub-category/{subCategoryId}")
    public APIResponse<CategoryResponse> getBySubCategoryId(@PathVariable String subCategoryId) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.getBySubCategoryId(subCategoryId))
                .build();
    }

    @Operation(summary = "Get all categories", description = "Retrieve a list of all categories.")
    @GetMapping("")
    public APIResponse<Page<CategoryResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<CategoryResponse> categoryResponsePage = categoryService.getAll(page, size);
        return APIResponse.<Page<CategoryResponse>>builder()
                .result(categoryResponsePage)
                .build();
    }

    @Operation(summary = "Update category", description = "Update a category by its ID.")
    @PutMapping("/{categoryId}")
    public APIResponse<CategoryResponse> update(@PathVariable String categoryId,
                                                @RequestBody CategoryRequest categoryRequest) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.update(categoryId, categoryRequest))
                .build();
    }

    @Operation(summary = "Delete category", description = "Delete a category by its ID.")
    @DeleteMapping("/{categoryId}")
    public APIResponse<Void> deleteById(@PathVariable String categoryId) {
        categoryService.deleteById(categoryId);
        return APIResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Search categories", description = "Search categories based on criteria.")
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
