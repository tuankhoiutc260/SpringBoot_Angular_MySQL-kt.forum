package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;

import java.util.List;

public interface SubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

//    SubCategoryResponse getById(String subCategoryID);
//
//    SubCategoryResponse update(String subCategoryId, SubCategoryRequest subCategoryRequest);
//
//    void deleteById(String subCategoryId);
//
//    List<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size);
//
    List<SubCategoryResponse> getAll();
//
    void indexSubCategory(SubCategoryDocument subCategoryDocument);

//    SubCategoryDocument createOrUpdateInElasticsearch(SubCategoryResponse subCategoryResponse);
//
//    List<SubCategoryDocument> searchSubCategoriesInElasticsearch(String title, int page, int size);
}
