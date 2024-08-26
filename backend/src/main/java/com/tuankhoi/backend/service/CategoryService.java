package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

public interface CategoryService {
    CategoryResponse create(CategoryRequest categoryRequest);

    CategoryResponse getById(String categoryId);

    Page<CategoryResponse> getAll(int page, int size);

    CategoryResponse update(String categoryId, CategoryRequest categoryRequest);

    void deleteById(String categoryId);

    Page<CategoryResponse> search(String query, int page, int size);

    void indexCategory(CategoryDocument categoryDocument);

    void deleteCategoryFromElasticsearch(String categoryID);
}
