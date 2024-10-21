package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.model.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    // For JPA
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toCategory(CategoryRequest categoryRequest);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    CategoryResponse toCategoryResponse(Category category);

    // For Elasticsearch
//    @Mapping(target = "subCategoryIds", source = "subCategories", qualifiedByName = "subCategoriesToIds")
    @Mapping(target = "subCategoryIds", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "localDateTimeToDate")
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "localDateTimeToDate")
    CategoryDocument toCategoryDocument(Category category);

    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "dateToLocalDateTime")
    Category fromCategoryDocument(CategoryDocument categoryDocument);

    CategoryResponse toCategoryResponseFromDocument(CategoryDocument categoryDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    void updateCategoryFromRequest(CategoryRequest categoryRequest, @MappingTarget Category category);

    @Named("subCategoriesToIds")
    default List<String> subCategoriesToIds(List<SubCategory> subCategories) {
        if (subCategories == null) {
            return null;
        }
        return subCategories.stream()
                .map(SubCategory::getId)
                .collect(Collectors.toList());
    }

    @Named("localDateTimeToDate")
    default Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC+7"));
        return Date.from(zonedDateTime.toInstant());
    }

    @Named("dateToLocalDateTime")
    default LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC+7"));
        return zonedDateTime.toLocalDateTime();
    }
}