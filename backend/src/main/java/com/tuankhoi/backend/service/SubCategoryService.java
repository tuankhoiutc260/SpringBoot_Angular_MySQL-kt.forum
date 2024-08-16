package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse findBySubCategoryId(String subCategoryID);

    List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size);

    List<SubCategoryResponse> findAll();

    SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest);

    void deleteBySubCategoryId(String subCategoryId);
}
