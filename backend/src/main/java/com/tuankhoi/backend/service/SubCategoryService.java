package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse getById(String subCategoryID);

    List<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size);

    List<SubCategoryResponse> getAll();

    SubCategoryResponse update(String subCategoryId, SubCategoryRequest subCategoryRequest);

    void deleteById(String subCategoryId);

    List<SubCategoryResponse> search(String query, int page, int size);

    void indexSubCategory(SubCategoryDocument subCategoryDocument);
}
