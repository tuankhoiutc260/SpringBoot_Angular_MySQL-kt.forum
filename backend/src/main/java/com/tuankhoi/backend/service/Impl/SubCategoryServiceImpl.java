package com.tuankhoi.backend.service.Impl;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubCategoryServiceImpl implements SubCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(SubCategoryServiceImpl.class);

    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;
    SubCategoryElasticsearchRepository subCategoryElasticsearchRepository;
    SubCategoryMapper subCategoryMapper;
    CloudinaryService cloudinaryService;
    ElasticsearchOperations elasticsearchOperations;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
        try {
            logger.info("Creating new SubCategory: {}", subCategoryRequest.getTitle());
            Category category = categoryRepository.findById(subCategoryRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

            SubCategory newSubCategory = subCategoryMapper.toSubCategory(subCategoryRequest);
            newSubCategory.setCategory(category);

            var imageFile = subCategoryRequest.getCoverImageFile();
            FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
            String imageFileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(imageFile.getOriginalFilename()));
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);
            newSubCategory.setImageUrl(cloudinaryResponse.getUrl());
            newSubCategory.setCloudinaryImageId(cloudinaryResponse.getPublicId());

            SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);

            indexSubCategory(subCategoryMapper.toSubCategoryDocument(savedSubCategory));

            logger.info("Successfully created SubCategory: {}", savedSubCategory.getId());
            return subCategoryMapper.toSubCategoryResponse(savedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            logger.error("Failed to create SubCategory due to database constraint", e);
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to create SubCategory", e);
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create Sub Category: " + e.getMessage());
        }
    }

    @Override
    public SubCategoryResponse getById(String subCategoryID) {
        logger.info("Fetching SubCategory by ID: {}", subCategoryID);
        return subCategoryRepository.findById(subCategoryID)
                .map(subCategoryMapper::toSubCategoryResponse)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Override
    public List<SubCategoryResponse> getByCategoryId(String categoryId, int page, int size) {
        logger.info("Fetching SubCategories for Category ID: {}, Page: {}, Size: {}", categoryId, page, size);
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_ID);
        }
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGINATION_PARAMETERS);
        }

        Pageable pageable = PageRequest.of(page, size);

        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        List<SubCategory> subCategoryList = subCategoryRepository
                .findByCategoryId(existingCategory.getId(), pageable);

        return subCategoryList.stream()
                .map(subCategoryMapper::toSubCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubCategoryResponse> getAll() {
        logger.info("Fetching all SubCategories");
        return subCategoryRepository.findAll()
                .stream()
                .map(subCategoryMapper::toSubCategoryResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
        logger.info("Updating SubCategory: {}", subCategoryRequestID);
        SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryRequestID)
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

            logger.info("Successfully updated SubCategory: {}", updatedSubCategory.getId());
            return subCategoryMapper.toSubCategoryResponse(updatedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            logger.error("Failed to update SubCategory due to database constraint", e);
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to update Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to update SubCategory", e);
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to update Sub Category: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public void deleteById(String subCategoryRequestId) {
        logger.info("Deleting SubCategory: {}", subCategoryRequestId);
        try {
            SubCategory subCategoryToDelete = subCategoryRepository.findById(subCategoryRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));

            if (subCategoryToDelete.getCloudinaryImageId() != null) {
                cloudinaryService.deleteImage(subCategoryToDelete.getCloudinaryImageId());
            }

            subCategoryRepository.deleteById(subCategoryToDelete.getId());
            deleteSubCategoryFromElasticsearch(subCategoryToDelete.getId());

            logger.info("Successfully deleted SubCategory: {}", subCategoryRequestId);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            logger.error("Failed to delete SubCategory due to database constraint", e);
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete Sub Category due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to delete SubCategory", e);
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete Sub Category: " + e.getMessage());
        }
    }

    @Override
    public List<SubCategoryResponse> search(String query, int page, int size) {
        logger.info("Searching SubCategories with query: {}, Page: {}, Size: {}", query, page, size);
        MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
                .query(query)
                .fields("title", "description")
                .fuzziness("AUTO")
                .build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(multiMatchQuery))
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<SubCategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, SubCategoryDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> subCategoryMapper.toSubCategoryResponseFromDocument(hit.getContent()))
                .collect(Collectors.toList());
    }

    @Async
    @Override
    public void indexSubCategory(SubCategoryDocument subCategoryDocument) {
        try {
            subCategoryElasticsearchRepository.save(subCategoryDocument);
            logger.info("Successfully indexed SubCategory: {}", subCategoryDocument.getId());
        } catch (Exception e) {
            logger.error("Failed to index SubCategory: {}", subCategoryDocument.getId(), e);
            throw new AppException(ErrorCode.ELASTICSEARCH_INDEXING_ERROR, "Failed to Index Sub Category in Elasticsearch: " + e.getMessage());
        }
    }

    @Async
    public void deleteSubCategoryFromElasticsearch(String id) {
        try {
            subCategoryElasticsearchRepository.deleteById(id);
            logger.info("Successfully deleted SubCategory from Elasticsearch: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete SubCategory from Elasticsearch: {}", id, e);
        }
    }
}