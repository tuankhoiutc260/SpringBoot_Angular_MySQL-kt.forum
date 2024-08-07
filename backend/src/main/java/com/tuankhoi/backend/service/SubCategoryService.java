package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse findById(String subCategoryID);

    List<SubCategoryResponse> findAll();

    SubCategoryResponse update(Integer subCategoryRequestID, SubCategoryRequest subCategoryRequest);

    void deleteById(String subCategoryRequest);
}
