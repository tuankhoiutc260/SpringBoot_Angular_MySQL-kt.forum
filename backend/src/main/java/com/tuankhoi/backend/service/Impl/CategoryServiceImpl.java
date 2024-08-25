package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.CategoryMapper;
import com.tuankhoi.backend.repository.Elasticsearch.CategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Jpa.CategoryRepository;
import com.tuankhoi.backend.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryElasticsearchRepository categoryElasticsearchRepository;

    CategoryMapper categoryMapper;

    ElasticsearchOperations elasticsearchOperations;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        try {
            Category newCategory = categoryMapper.toCategory(categoryRequest);
            Category savedCategory = categoryRepository.save(newCategory);

            CategoryDocument categoryDocument = categoryMapper.toCategoryDocument(newCategory);
            indexCategory(categoryDocument);

            return categoryMapper.toCategoryResponse(savedCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Create Category due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Create Category", e);
        }
    }

    @Override
    public CategoryResponse getById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse update(String categoryID, CategoryRequest categoryRequest) {
        try {
            Category existingCategory = categoryRepository.findById(categoryID)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

            categoryMapper.updateCategoryFromRequest(categoryRequest, existingCategory);
            Category updatedCategory = categoryRepository.save(existingCategory);

            CategoryDocument categoryDocument = categoryMapper.toCategoryDocument(updatedCategory);
            indexCategory(categoryDocument);

            return categoryMapper.toCategoryResponse(updatedCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Update Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to Update Category", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(String categoryRequestID) {
        try {
            Category categoryToDelete = categoryRepository.findById(categoryRequestID)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

            categoryRepository.deleteById(categoryToDelete.getId());
            categoryElasticsearchRepository.deleteById(categoryToDelete.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Delete Category due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Delete Category", e);
        }
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> search(String query) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields("title", "description")
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .build();

        SearchHits<CategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, CategoryDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(hit -> categoryMapper.toCategoryResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public void indexCategory(CategoryDocument categoryDocument) {
        try {
            categoryElasticsearchRepository.save(categoryDocument);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Index Category in Elasticsearch: ", e);
        }
    }
}
