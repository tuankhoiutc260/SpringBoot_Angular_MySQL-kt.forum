package com.tuankhoi.backend.service.Impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import com.tuankhoi.backend.dto.document.CategoryDocument;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.SubCategoryMapper;
import com.tuankhoi.backend.repository.Elasticsearch.CategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Elasticsearch.SubCategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Jpa.CategoryRepository;
import com.tuankhoi.backend.repository.Jpa.SubCategoryRepository;
import com.tuankhoi.backend.service.CloudinaryService;
import com.tuankhoi.backend.service.SubCategoryService;
import com.tuankhoi.backend.untils.FileUploadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SubCategoryServiceImpl implements SubCategoryService {
    CategoryRepository categoryRepository;
    CategoryElasticsearchRepository categoryElasticsearchRepository;

    SubCategoryRepository subCategoryRepository;
    SubCategoryElasticsearchRepository subCategoryElasticsearchRepository;

    SubCategoryMapper subCategoryMapper;

    CloudinaryService cloudinaryService;
    ElasticsearchOperations elasticsearchOperations;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
        try {
            categoryRepository.findById(subCategoryRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

            SubCategory newSubCategory = subCategoryMapper.toSubCategory(subCategoryRequest);

            var imageFile = subCategoryRequest.getCoverImageFile();

            FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
            String imageFileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(imageFile.getOriginalFilename()));

            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);

            newSubCategory.setImageUrl(cloudinaryResponse.getUrl());
            newSubCategory.setCloudinaryImageId(cloudinaryResponse.getPublicId());

            SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);

            SubCategoryDocument subCategoryDocument = subCategoryMapper.toSubCategoryDocument(savedSubCategory);
            indexSubCategory(subCategoryDocument);

            return subCategoryMapper.toSubCategoryResponse(savedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Sub Category due to database constraint: " + e.getMessage(), e);
        }
    }

    @Override
    public SubCategoryResponse getById(String subCategoryID) {
        return subCategoryElasticsearchRepository.findById(subCategoryID)
                .map(subCategoryMapper::toSubCategoryResponseFromDocument)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Override
    public List<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_ID);
        }

        Pageable pageable = PageRequest.of(page, size);

        CategoryDocument existingCategoryDocument = categoryElasticsearchRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        List<SubCategoryDocument> subCategoryDocumentResponseList = subCategoryElasticsearchRepository
                .findByCategoryId(existingCategoryDocument.getId(), pageable);

        return subCategoryDocumentResponseList.stream()
                .map(subCategoryMapper::toSubCategoryResponseFromDocument)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryResponse> getAll() {
        return StreamSupport.stream(subCategoryElasticsearchRepository.findAll().spliterator(), false)
                .map(subCategoryMapper::toSubCategoryResponseFromDocument)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
        try {
            SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryRequestID)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
            subCategoryMapper.updateSubCategoryFromRequest(subCategoryRequest, existingSubCategory);
            SubCategory updatedSubCategory = subCategoryRepository.save(existingSubCategory);

            SubCategoryDocument subCategoryDocument = subCategoryMapper.toSubCategoryDocument(updatedSubCategory);
            indexSubCategory(subCategoryDocument);

            return subCategoryMapper.toSubCategoryResponse(updatedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to index Category in Elasticsearch: " + e.getMessage(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(String subCategoryRequestId) {
        try {
            SubCategory subCategoryToDelete = subCategoryRepository.findById(subCategoryRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

            subCategoryRepository.deleteById(subCategoryToDelete.getId());
            subCategoryElasticsearchRepository.deleteById(subCategoryToDelete.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete Sub Category due to database constraint: " + e.getMessage());
        }
    }

    @Override
    public List<SubCategoryResponse> search(String query) {
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields("title", "description")
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .build();

        SearchHits<SubCategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, SubCategoryDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> subCategoryMapper.toSubCategoryResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public void indexSubCategory(SubCategoryDocument subCategoryDocument) {
        subCategoryElasticsearchRepository.save(subCategoryDocument);
    }
}
