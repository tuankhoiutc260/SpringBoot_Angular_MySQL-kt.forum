package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.SubCategory;
import com.tuankhoi.backend.service.SubCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
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

    @GetMapping("/{subCategoryID}")
    public APIResponse<SubCategoryResponse> findById(@PathVariable String subCategoryID) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.findBySubCategoryId(subCategoryID))
                .build();
    }

    @GetMapping("/category/{categoryID}")
    public APIResponse<List<SubCategoryResponse>> findByCategoryId(@PathVariable String categoryID,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size) {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryService.findByCategoryId(categoryID, page, size))
                .build();
    }

    @GetMapping("/{id}/cover-image")
    public ResponseEntity<byte[]> getSubCategoryCoverImage(@PathVariable String subcategoryId) {
        SubCategoryResponse subCategory = subCategoryService.findBySubCategoryId(subcategoryId);
        if (subCategory != null && subCategory.getCoverImage() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(subCategory.getCoverImage());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("")
    public APIResponse<List<SubCategoryResponse>> findAll() {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryService.findAll())
                .build();
    }

    @PutMapping("/{subCategoryId}")
    public APIResponse<SubCategoryResponse> update(@PathVariable String subCategoryId, @RequestBody SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.update(subCategoryId, subCategoryRequest))
                .build();
    }

    @DeleteMapping("/{subcategoryId}")
    public APIResponse<Void> deleteBySubCategoryId(@PathVariable String subcategoryId) {
        subCategoryService.deleteBySubCategoryId(subcategoryId);
        return APIResponse.<Void>builder()
                .build();
    }
}
