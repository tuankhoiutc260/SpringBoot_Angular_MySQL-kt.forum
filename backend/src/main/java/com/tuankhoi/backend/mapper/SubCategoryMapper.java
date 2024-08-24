package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
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

@Mapper(componentModel = "spring")
public interface SubCategoryMapper {
    // For JPA
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    SubCategory toSubCategory(SubCategoryRequest subCategoryRequest);

    SubCategoryResponse toSubCategoryResponse(SubCategory subCategory);

    // For Elasticsearch
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "localDateTimeToDate")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "localDateTimeToDate")
    SubCategoryDocument toSubCategoryDocument(SubCategory subCategory);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "dateToLocalDateTime")
    SubCategory toSubCategoryFromDocument(SubCategoryDocument subCategoryDocument);

    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "dateToLocalDateTime")
    SubCategoryResponse toSubCategoryResponseFromDocument(SubCategoryDocument subCategoryDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateSubCategoryFromRequest(SubCategoryRequest subCategoryRequest, @MappingTarget SubCategory subCategory);

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
