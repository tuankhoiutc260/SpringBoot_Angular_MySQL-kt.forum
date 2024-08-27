package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.SubCategoryMapper;
import com.tuankhoi.backend.repository.Elasticsearch.SubCategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Jpa.CategoryRepository;
import com.tuankhoi.backend.repository.Jpa.SubCategoryRepository;
import com.tuankhoi.backend.service.CloudinaryService;
import com.tuankhoi.backend.service.SubCategoryService;
import com.tuankhoi.backend.untils.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryServiceImpl implements SubCategoryService {
    static final String ELASTICSEARCH_TITLE_FIELD = "title";
    static final String ELASTICSEARCH_DESCRIPTION_FIELD = "description";

    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;
    SubCategoryElasticsearchRepository subCategoryElasticsearchRepository;
    SubCategoryMapper subCategoryMapper;
    CloudinaryService cloudinaryService;
    ElasticsearchOperations elasticsearchOperations;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subCategory", allEntries = true),
            @CacheEvict(value = "subCategories", allEntries = true),
            @CacheEvict(value = "subCategorySearch", allEntries = true)
    })
    @Override
    public SubCategoryResponse create(@Valid SubCategoryRequest subCategoryRequest) {
        Category existingCategory = categoryRepository.findById(subCategoryRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        try {
            SubCategory newSubCategory = subCategoryMapper.toSubCategory(subCategoryRequest);
            newSubCategory.setCategory(existingCategory);

            var imageFile = subCategoryRequest.getCoverImageFile();
            FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
            String imageFileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(imageFile.getOriginalFilename()));
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);
            newSubCategory.setImageUrl(cloudinaryResponse.getUrl());
            newSubCategory.setCloudinaryImageId(cloudinaryResponse.getPublicId());

            SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);

            indexSubCategory(subCategoryMapper.toSubCategoryDocument(savedSubCategory));

            return subCategoryMapper.toSubCategoryResponse(savedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create Sub Category: " + e.getMessage());
        }
    }

    @Cacheable(value = "subCategory", key = "#subCategoryId", unless = "#result == null")
    @Override
    public SubCategoryResponse getById(String subCategoryId) {
        return subCategoryRepository.findById(subCategoryId)
                .map(subCategoryMapper::toSubCategoryResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Cacheable(value = "subCategories", key = "'categoryId:' + #categoryId", unless = "#result.isEmpty()")
    @Override
    public Page<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_ID);
        }
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGINATION_PARAMETERS);
        }

        Pageable pageable = PageRequest.of(page, size);

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        Page<SubCategory> subCategoryPage = subCategoryRepository
                .findByCategoryId(existingCategory.getId(), pageable);

        return subCategoryPage.map(subCategoryMapper::toSubCategoryResponse);
    }

    @Cacheable(value = "subCategories", key = "'all:page:' + #page + ',size:' + #size", unless = "#result.getContent().isEmpty()")
    @Override
    public Page<SubCategoryResponse> getAll(int page, int size) {
        System.out.println("get");

        Pageable pageable = PageRequest.of(page, size);
        Page<SubCategory> subCategoryPage = subCategoryRepository.findAll(pageable);
        return subCategoryPage.map(subCategoryMapper::toSubCategoryResponse);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subCategory", key = "#subCategoryId"),
            @CacheEvict(value = "subCategories", allEntries = true),
            @CacheEvict(value = "subCategorySearch", allEntries = true)
    })
    @Override
    public SubCategoryResponse update(String subCategoryId, @Valid SubCategoryRequest subCategoryRequest) {
        SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        try {
            if (subCategoryRequest.getCoverImageFile() != null && !subCategoryRequest.getCoverImageFile().isEmpty()) {
                var imageFile = subCategoryRequest.getCoverImageFile();
                FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
                String imageFileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(imageFile.getOriginalFilename()));

                if (existingSubCategory.getCloudinaryImageId() != null) {
                    cloudinaryService.deleteImage(existingSubCategory.getCloudinaryImageId());
                }

                CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);
                existingSubCategory.setImageUrl(cloudinaryResponse.getUrl());
                existingSubCategory.setCloudinaryImageId(cloudinaryResponse.getPublicId());
            }

            subCategoryMapper.updateSubCategoryFromRequest(subCategoryRequest, existingSubCategory);
            SubCategory updatedSubCategory = subCategoryRepository.save(existingSubCategory);

            indexSubCategory(subCategoryMapper.toSubCategoryDocument(updatedSubCategory));

            return subCategoryMapper.toSubCategoryResponse(updatedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Update Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update Sub Category: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "subCategory", key = "#subCategoryId"),
            @CacheEvict(value = "subCategories", allEntries = true),
            @CacheEvict(value = "subCategorySearch", allEntries = true)
    })
    @Override
    public void deleteById(String subCategoryId) {
        SubCategory subCategoryToDelete = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

        try {
            if (subCategoryToDelete.getCloudinaryImageId() != null) {
                cloudinaryService.deleteImage(subCategoryToDelete.getCloudinaryImageId());
            }

            subCategoryRepository.deleteById(subCategoryToDelete.getId());
            deleteSubCategoryFromElasticsearch(subCategoryToDelete.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Sub Category: " + e.getMessage());
        }
    }

    @Cacheable(value = "subCategorySearch", key = "'query:' + #query + ',page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<SubCategoryResponse> search(String query, int page, int size) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields(ELASTICSEARCH_TITLE_FIELD, ELASTICSEARCH_DESCRIPTION_FIELD)
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<SubCategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, SubCategoryDocument.class);

        List<SubCategoryResponse> subCategoryResponseList = searchHits.getSearchHits().stream()
                .map(hit -> subCategoryMapper.toSubCategoryResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());

        long totalHits = searchHits.getTotalHits();
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(subCategoryResponseList, pageable, totalHits);
    }

    @Async
    @Override
    public void indexSubCategory(SubCategoryDocument subCategoryDocument) {
        try {
            subCategoryElasticsearchRepository.save(subCategoryDocument);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_INDEXING_ERROR, "Failed to Index Sub Category in Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Index Sub Category in Elasticsearch: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void deleteSubCategoryFromElasticsearch(String subCategoryId) {
        try {
            subCategoryElasticsearchRepository.deleteById(subCategoryId);
        } catch (ElasticsearchException e) {
            throw new AppException(ErrorCode.ELASTICSEARCH_ERROR, "Failed to Delete Sub Category from Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Sub Category from Elasticsearch: " + e.getMessage());
        }
    }
}
