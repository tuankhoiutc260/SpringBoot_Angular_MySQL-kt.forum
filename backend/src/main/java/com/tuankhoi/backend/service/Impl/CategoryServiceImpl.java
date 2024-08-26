package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    static final String ELASTICSEARCH_TITLE_FIELD = "title";
    static final String ELASTICSEARCH_DESCRIPTION_FIELD = "description";

    CategoryRepository categoryRepository;
    CategoryElasticsearchRepository categoryElasticsearchRepository;
    CategoryMapper categoryMapper;
    ElasticsearchOperations elasticsearchOperations;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        try {
            Category newCategory = categoryMapper.toCategory(categoryRequest);
            Category savedCategory = categoryRepository.save(newCategory);

            indexCategory(categoryMapper.toCategoryDocument(newCategory));

            return categoryMapper.toCategoryResponse(savedCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create Category: " + e.getMessage());
        }
    }

    @Cacheable(value = "category", key = "#categoryId")
    @Override
    public CategoryResponse getById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryMapper::toCategoryResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
    }

    @Cacheable(value = "categories", key = "#page + '-' + #size")
    @Override
    public Page<CategoryResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.map(categoryMapper::toCategoryResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "categories", allEntries = true)
    })
    @Override
    public CategoryResponse update(String categoryID, CategoryRequest categoryRequest) {
        Category existingCategory = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        try {
            categoryMapper.updateCategoryFromRequest(categoryRequest, existingCategory);
            Category updatedCategory = categoryRepository.save(existingCategory);

            indexCategory(categoryMapper.toCategoryDocument(updatedCategory));

            return categoryMapper.toCategoryResponse(updatedCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Update Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update Category" + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "categories", allEntries = true)
    })
    @Override
    public void deleteById(String categoryRequestID) {
        Category categoryToDelete = categoryRepository.findById(categoryRequestID)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        try {
            categoryRepository.deleteById(categoryToDelete.getId());
            deleteCategoryFromElasticsearch(categoryToDelete.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete Category due to database constraint", e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Category: " + e.getMessage());
        }
    }

    @Cacheable(value = "categorySearch", key = "#query + '-' + #page + '-' + #size")
    @Override
    public Page<CategoryResponse> search(String query, int page, int size) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields(ELASTICSEARCH_TITLE_FIELD, ELASTICSEARCH_DESCRIPTION_FIELD)
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .build();

        SearchHits<CategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, CategoryDocument.class);

        List<CategoryResponse> subCategoryResponses = searchHits.getSearchHits().stream()
                .map(hit -> categoryMapper.toCategoryResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());

        long totalHits = searchHits.getTotalHits();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(subCategoryResponses, pageable, totalHits);
    }

    @Override
    public void indexCategory(CategoryDocument categoryDocument) {
        try {
            categoryElasticsearchRepository.save(categoryDocument);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_INDEXING_ERROR, "Failed to Index Category in Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Index Category in Elasticsearch: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void deleteCategoryFromElasticsearch(String categoryID) {
        try {
            categoryElasticsearchRepository.deleteById(categoryID);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_ERROR, "Failed to delete Sub Category from Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to delete Sub Category from Elasticsearch: " + e.getMessage());
        }
    }
}
