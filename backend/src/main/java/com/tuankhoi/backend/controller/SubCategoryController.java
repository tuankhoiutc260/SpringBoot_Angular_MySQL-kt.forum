package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.service.SubCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sub-categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryController {
    SubCategoryService subCategoryService;

    @PostMapping
    public APIResponse<SubCategoryResponse> create(@ModelAttribute SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.create(subCategoryRequest))
                .build();
    }

    @GetMapping("/{subCategoryID}")
    public APIResponse<SubCategoryResponse> getById(@PathVariable String subCategoryID) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.getById(subCategoryID))
                .build();
    }

    @GetMapping("/category/{categoryID}")
    public APIResponse<List<SubCategoryResponse>> getByCategoryId(@PathVariable String categoryID,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "50") int size) {
        List<SubCategoryResponse> subCategoryResponseList = subCategoryService.getByCategoryId(categoryID, page, size);
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryResponseList)
                .totalRecords(subCategoryResponseList.size())
                .build();
    }

    @GetMapping("")
    public APIResponse<List<SubCategoryResponse>> getAll() {
        List<SubCategoryResponse> subCategoryResponseList = subCategoryService.getAll();
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategoryResponseList)
                .totalRecords(subCategoryResponseList.size())
                .build();
    }

    @PutMapping("/{subCategoryId}")
    public APIResponse<SubCategoryResponse> update(@PathVariable String subCategoryId, @ModelAttribute SubCategoryRequest subCategoryRequest) {
        return APIResponse.<SubCategoryResponse>builder()
                .result(subCategoryService.update(subCategoryId, subCategoryRequest))
                .build();
    }

    @DeleteMapping("/{subcategoryId}")
    public APIResponse<Void> deleteById(@PathVariable String subcategoryId) {
        subCategoryService.deleteById(subcategoryId);
        return APIResponse.<Void>builder()
                .build();
    }

    @GetMapping("/search")
    public APIResponse<List<SubCategoryResponse>> search(@RequestParam String query,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        List<SubCategoryResponse> subCategorySearchedList = subCategoryService.search(query, page, size);
        return APIResponse.<List<SubCategoryResponse>>builder()
                .result(subCategorySearchedList)
                .totalRecords(subCategorySearchedList.size())
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
