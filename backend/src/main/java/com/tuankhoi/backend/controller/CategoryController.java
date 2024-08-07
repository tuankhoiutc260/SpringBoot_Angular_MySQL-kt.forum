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

    @GetMapping("/{categoryID}")
    public APIResponse<CategoryResponse> findById(@PathVariable String categoryID) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.findById(categoryID))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<CategoryResponse>> findAll() {
        return APIResponse.<List<CategoryResponse>>builder()
                .result(categoryService.findAll())
                .build();
    }

    @PutMapping("/{categoryID}")
    public APIResponse<CategoryResponse> update(@PathVariable String categoryID, @RequestBody CategoryRequest categoryRequest) {
        return APIResponse.<CategoryResponse>builder()
                .result(categoryService.update(categoryID, categoryRequest))
                .build();
    }

    @DeleteMapping("/{categoryRequestID}")
    public APIResponse<Void> delete(@PathVariable String categoryRequestID) {
        categoryService.deleteById(categoryRequestID);
        return APIResponse.<Void>builder()
                .build();
    }
}


