package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.service.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sub-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Sub Category Controller")
public class SubCategoryController {
    SubCategoryService subCategoryService;

    @Operation(summary = "Create new sub-category", description = "Create a new sub-category.")
    @PostMapping
    public APIResponse<SubCategoryResponse> create(@ModelAttribute SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.create(subCategoryRequest))
                .build();
    }

    @Operation(summary = "Get sub-category by ID", description = "Retrieve a sub-category by its ID.")
    @GetMapping("/{subCategoryId}")
    public APIResponse<SubCategoryResponse> getById(@PathVariable String subCategoryId) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.getById(subCategoryId))
                .build();
    }

    @Operation(summary = "Get sub-categories by category ID", description = "Retrieve all sub-categories under a specific category ID.")
    @GetMapping("/category/{categoryId}")
    public APIResponse<Page<SubCategoryResponse>> getByCategoryId(@PathVariable String categoryId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Page<SubCategoryResponse> subCategoryResponsePage = subCategoryService.getByCategoryId(categoryId, page, size);
        return APIResponse.<Page<SubCategoryResponse>>builder()
                .result(subCategoryResponsePage)
                .build();
    }

    @Operation(summary = "Get all sub-categories", description = "Retrieve a list of all sub-categories.")
    @GetMapping("")
    public APIResponse<Page<SubCategoryResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<SubCategoryResponse> subCategoryResponsePage = subCategoryService.getAll(page, size);
        return APIResponse.<Page<SubCategoryResponse>>builder()
                .result(subCategoryResponsePage)
                .build();
    }

    @Operation(summary = "Update sub-category", description = "Update a sub-category by its ID.")
    @PutMapping("/{subCategoryId}")
    public APIResponse<SubCategoryResponse> update(@PathVariable String subCategoryId,
                                                   @ModelAttribute SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.update(subCategoryId, subCategoryRequest))
                .build();
    }

    @Operation(summary = "Delete sub-category", description = "Delete a sub-category by its ID.")
    @DeleteMapping("/{subcategoryId}")
    public APIResponse<Void> deleteById(@PathVariable String subcategoryId) {
        subCategoryService.deleteById(subcategoryId);
        return APIResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Search sub-categories", description = "Search sub-categories based on criteria.")
    @GetMapping("/search")
    public APIResponse<Page<SubCategoryResponse>> search(@RequestParam String query,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<SubCategoryResponse> subCategoryResponsePage = subCategoryService.search(query, page, size);
        return APIResponse.<Page<SubCategoryResponse>>builder()
                .result(subCategoryResponsePage)
                .build();
    }

//    @GetMapping("/{subcategoryId}/cover-image")
//    public ResponseEntity<byte[]> getSubCategoryCoverImage(@PathVariable String subcategoryId) {
//        SubCategoryResponse subCategory = iSubCategoryService.findBySubCategoryId(subcategoryId);
//        if (subCategory != null && subCategory.getCoverImage() != null) {
//            byte[] imageBytes = Base64.getDecoder().decode(subCategory.getCoverImage());
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.IMAGE_JPEG);
//            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//        }
//        return ResponseEntity.notFound().build();
//    }
}
