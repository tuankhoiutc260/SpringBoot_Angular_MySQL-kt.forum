package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {
    SubCategory toCategory(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse toResponse(SubCategory subCategory);

    default SubCategoryResponse toSubCategoryResponseWithTotalPosts(SubCategory subCategory) {
        SubCategoryResponse subCategoryResponse = toResponse(subCategory);
        subCategoryResponse.setTotalPosts(subCategory.getPosts().size());
        return subCategoryResponse;
    }



    void updateSubCategory(@MappingTarget SubCategory subCategory, SubCategoryRequest subCategoryRequest);
}
