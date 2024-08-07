package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.entity.Category;
import com.tuankhoi.backend.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);

    void updateCategory(@MappingTarget Category category, CategoryRequest categoryRequest);

}
