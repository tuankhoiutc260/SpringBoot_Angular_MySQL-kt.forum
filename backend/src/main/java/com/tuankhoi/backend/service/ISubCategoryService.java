package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
//import com.tuankhoi.backend.model.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.SubCategory;

import java.util.List;

public interface ISubCategoryService {
    SubCategoryResponse create(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse findBySubCategoryId(String subCategoryID);

    List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size);

    List<SubCategoryResponse> findAll();

    SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest);

    void deleteBySubCategoryId(String subCategoryId);

//    SubCategoryDocument createOrUpdateInElasticsearch(SubCategoryResponse subCategoryResponse);
//
//    List<SubCategoryDocument> searchSubCategoriesInElasticsearch(String title, int page, int size);
}
