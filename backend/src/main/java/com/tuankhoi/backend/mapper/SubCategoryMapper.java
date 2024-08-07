package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.SubCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {
    SubCategory toCategory(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse toResponse(SubCategory subCategory);
}
