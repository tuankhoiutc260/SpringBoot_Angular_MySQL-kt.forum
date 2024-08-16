package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.SubCategory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class SubCategoryMapperImpl implements SubCategoryMapper {

    @Override
    public SubCategory toCategory(SubCategoryRequest subCategoryRequest) {
        if ( subCategoryRequest == null ) {
            return null;
        }

        SubCategory.SubCategoryBuilder subCategory = SubCategory.builder();

        subCategory.title( subCategoryRequest.getTitle() );
        subCategory.description( subCategoryRequest.getDescription() );

        return subCategory.build();
    }

    @Override
    public SubCategoryResponse toResponse(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        SubCategoryResponse.SubCategoryResponseBuilder subCategoryResponse = SubCategoryResponse.builder();

        subCategoryResponse.id( subCategory.getId() );
        subCategoryResponse.title( subCategory.getTitle() );
        subCategoryResponse.description( subCategory.getDescription() );
        subCategoryResponse.coverImage( subCategory.getCoverImage() );
        subCategoryResponse.createdBy( subCategory.getCreatedBy() );
        subCategoryResponse.createdDate( subCategory.getCreatedDate() );
        subCategoryResponse.lastModifiedBy( subCategory.getLastModifiedBy() );
        subCategoryResponse.lastModifiedDate( subCategory.getLastModifiedDate() );

        return subCategoryResponse.build();
    }

    @Override
    public void updateSubCategory(SubCategory subCategory, SubCategoryRequest subCategoryRequest) {
        if ( subCategoryRequest == null ) {
            return;
        }

        subCategory.setTitle( subCategoryRequest.getTitle() );
        subCategory.setDescription( subCategoryRequest.getDescription() );
    }
}
