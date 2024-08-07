package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest categoryRequest);

    CategoryResponse findById(String categoryID);

    List<CategoryResponse> findAll();

    CategoryResponse update(String categoryRequestID, CategoryRequest categoryRequest);

    void deleteById(String categoryRequestID);
}
