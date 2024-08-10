package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.service.SubCategoryService;
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
@RequestMapping("/api/v1/sub-categories")
public class SubCategoryController {
    SubCategoryService subCategoryService;

    @PostMapping
    public APIResponse<SubCategoryResponse> create(@RequestBody SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.create(subCategoryRequest))
                .build();
    }


//    @GetMapping("/{categoryID}")
//    public APIResponse<CategoryResponse> findById(@PathVariable String categoryID) {
//        return APIResponse.<CategoryResponse>builder()
//                .result(categoryService.findById(categoryID))
//                .build();
//    }
//

    @GetMapping("/category/{categoryID}")
    public APIResponse<List<SubCategoryResponse>> findById(@PathVariable String categoryID,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryService.findByCategoryId(categoryID, page, size))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<SubCategoryResponse>> findAll() {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryService.findAll())
                .build();
    }
//
//    @PutMapping("/{categoryID}")
//    public APIResponse<CategoryResponse> update(@PathVariable String categoryID, @RequestBody CategoryRequest categoryRequest) {
//        return APIResponse.<CategoryResponse>builder()
//                .result(categoryService.update(categoryID, categoryRequest))
//                .build();
//    }
//
//    @DeleteMapping("/{categoryRequestID}")
//    public APIResponse<Void> delete(@PathVariable String categoryRequestID) {
//        categoryService.deleteById(categoryRequestID);
//        return APIResponse.<Void>builder()
//                .build();
//    }


}
