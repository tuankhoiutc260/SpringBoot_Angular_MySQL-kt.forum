package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import org.springframework.data.domain.Page;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse getById(String subCategoryID);

    Page<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size);

    Page<SubCategoryResponse> getAll(int page, int size);

    SubCategoryResponse update(String subCategoryId, SubCategoryRequest subCategoryRequest);

    void deleteById(String subCategoryId);

    Page<SubCategoryResponse> search(String query, int page, int size);

    void indexSubCategory(SubCategoryDocument subCategoryDocument);

    void deleteSubCategoryFromElasticsearch(String subCategoryID);
}