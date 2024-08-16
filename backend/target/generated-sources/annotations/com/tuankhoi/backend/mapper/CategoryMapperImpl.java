package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.entity.Category;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toCategory(CategoryRequest categoryRequest) {
        if ( categoryRequest == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.title( categoryRequest.getTitle() );
        category.description( categoryRequest.getDescription() );

        return category.build();
    }

    @Override
    public CategoryResponse toResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.id( category.getId() );
        categoryResponse.title( category.getTitle() );
        categoryResponse.description( category.getDescription() );
        categoryResponse.createdBy( category.getCreatedBy() );
        categoryResponse.createdDate( category.getCreatedDate() );
        categoryResponse.lastModifiedBy( category.getLastModifiedBy() );
        categoryResponse.lastModifiedDate( category.getLastModifiedDate() );

        return categoryResponse.build();
    }

    @Override
    public void updateCategory(Category category, CategoryRequest categoryRequest) {
        if ( categoryRequest == null ) {
            return;
        }

        category.setTitle( categoryRequest.getTitle() );
        category.setDescription( categoryRequest.getDescription() );
    }
}
