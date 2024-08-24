package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest categoryRequest);

    CategoryResponse getById(String categoryId);

    CategoryResponse update(String categoryId, CategoryRequest categoryRequest);

    void deleteById(String categoryId);

    List<CategoryResponse> getAll();

    List<CategoryResponse> search(String query);

    void indexCategory(CategoryDocument categoryDocument);
}
