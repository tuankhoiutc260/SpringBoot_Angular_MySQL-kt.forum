package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.model.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ISubCategoryMapper {
    SubCategory toSubCategory(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse toResponse(SubCategory subCategory);

    default SubCategoryResponse toSubCategoryResponseWithTotalPosts(SubCategory subCategory) {
        SubCategoryResponse subCategoryResponse = toResponse(subCategory);
//        subCategoryResponse.setTotalPosts(subCategory.getPosts().size());
        subCategoryResponse.setTotalPosts(subCategory.getPosts() != null ? subCategory.getPosts().size() : 0);

        return subCategoryResponse;
    }

    void updateSubCategory(@MappingTarget SubCategory subCategory, SubCategoryRequest subCategoryRequest);
}
