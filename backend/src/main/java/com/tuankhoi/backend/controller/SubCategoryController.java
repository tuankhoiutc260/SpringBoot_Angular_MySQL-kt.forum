package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.service.ISubCategoryService;
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
    ISubCategoryService iSubCategoryService;

//    @PostMapping
//    public APIResponse<SubCategoryResponse> create(@RequestBody SubCategoryRequest subCategoryRequest) {
//        SubCategoryResponse savedSubCategory = iSubCategoryService.create(subCategoryRequest);
//        return APIResponse.<SubCategoryResponse>builder()
//                .result(savedSubCategory)
//                .build();
//    }

    @PostMapping
    public APIResponse<SubCategoryResponse> create(@RequestBody SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(iSubCategoryService.create(subCategoryRequest))
                .build();
    }

    @GetMapping("/{subCategoryID}")
    public APIResponse<SubCategoryResponse> findById(@PathVariable String subCategoryID) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(iSubCategoryService.findBySubCategoryId(subCategoryID))
                .build();
    }

    @GetMapping("/category/{categoryID}")
    public APIResponse<List<SubCategoryResponse>> findByCategoryId(@PathVariable String categoryID,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size) {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(iSubCategoryService.findByCategoryId(categoryID, page, size))
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

    @GetMapping("")
    public APIResponse<List<SubCategoryResponse>> findAll() {
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(iSubCategoryService.findAll())
                .build();
    }

    @PutMapping("/{subCategoryId}")
    public APIResponse<SubCategoryResponse> update(@PathVariable String subCategoryId, @RequestBody SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(iSubCategoryService.update(subCategoryId, subCategoryRequest))
                .build();
    }

    @DeleteMapping("/{subcategoryId}")
    public APIResponse<Void> deleteBySubCategoryId(@PathVariable String subcategoryId) {
        iSubCategoryService.deleteBySubCategoryId(subcategoryId);
        return APIResponse.<Void>builder()
                .build();
    }
}
