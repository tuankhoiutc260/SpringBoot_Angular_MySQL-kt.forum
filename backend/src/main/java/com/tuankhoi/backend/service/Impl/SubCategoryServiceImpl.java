package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.controller.SubCategoryController;
import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.SubCategoryMapper;
//import com.tuankhoi.backend.repository.ElasticSearch.ISubCategoryElasticsearchRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SubCategoryServiceImpl implements SubCategoryService {
    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;
    SubCategoryMapper subCategoryMapper;
    SubCategoryElasticsearchRepository subCategoryElasticsearchRepository;
    CloudinaryService cloudinaryService;

//    @Override
    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
        try {
            Category existingCategory = categoryRepository.findById(subCategoryRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

            SubCategory newSubCategory = subCategoryMapper.toSubCategory(subCategoryRequest);
//            newSubCategory.setCategory(existingCategory);

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

//    @Override
//    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
//        try {
//            Category existingCategory = ICategoryRepository.findById(subCategoryRequest.getCategoryId())
//                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
//            SubCategory newSubCategory = ISubCategoryMapper.toSubCategory(subCategoryRequest);
//            newSubCategory.setCategory(existingCategory);
//            SubCategory savedSubCategory = ISubCategoryRepository.save(newSubCategory);
//            iSubCategoryElasticsearchRepository.save(savedSubCategory);
//            return ISubCategoryMapper.toSubCategoryResponseWithTotalPosts(savedSubCategory);
////            Category existingCategory = ICategoryRepository.findById(subCategoryRequest.getCategoryId())
////                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
////            SubCategory newSubCategory = ISubCategoryMapper.toSubCategory(subCategoryRequest);
////            newSubCategory.setCategory(existingCategory);
////            SubCategory savedSubCategory = ISubCategoryRepository.save(newSubCategory);
////            SubCategoryResponse subCategoryResponse = ISubCategoryMapper.toSubCategoryResponseWithTotalPosts(savedSubCategory);
//////            createOrUpdateInElasticsearch(subCategoryResponse);
////            return subCategoryResponse;
//        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
//            throw new IllegalArgumentException("Failed to create Sub Category due to database constraint: " + e.getMessage(), e);
//        }
//    }

//    @Override
//    public SubCategoryResponse findBySubCategoryId(String subCategoryID) {
//        return SubCategoryRepository.findById(subCategoryID)
//                .map(SubCategoryMapper::toSubCategoryResponseWithTotalPosts)
//                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
//    }
//
//    @Override
//    public List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Category existingCategory = CategoryRepository.findById(categoryId)
//                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
//        return SubCategoryRepository.findByCategoryId(existingCategory.getId(), pageable)
//                .stream()
//                .map(SubCategoryMapper::toSubCategoryResponseWithTotalPosts)
//                .toList();
//    }
//
    @Override
    public List<SubCategoryResponse> getAll() {
        return subCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(subCategoryMapper::toSubCategoryResponse)
                .toList();
    }
//
//    @Override
//    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
//        try {
//            SubCategory existingSubCategory = SubCategoryRepository.findById(subCategoryRequestID)
//                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
//            SubCategoryMapper.updateSubCategory(existingSubCategory, subCategoryRequest);
//            return SubCategoryMapper.toSubCategoryResponseWithTotalPosts(SubCategoryRepository.save(existingSubCategory));
//        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
//            throw new IllegalArgumentException("Failed to update Sub Category due to database constraint: " + e.getMessage());
//        }
//    }
//
//
//    @Override
//    public void deleteBySubCategoryId(String subCategoryRequestId) {
//        try {
//            SubCategory existingSubCategory = SubCategoryRepository.findById(subCategoryRequestId)
//                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
//            SubCategoryRepository.deleteById(existingSubCategory.getId());
//        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
//            throw new IllegalArgumentException("Failed to delete Sub Category due to database constraint: " + e.getMessage());
//        }
//    }

//    @Override
    public void indexSubCategory(SubCategoryDocument subCategoryDocument) {
        subCategoryElasticsearchRepository.save(subCategoryDocument);
    }
//
//    @Override
//    public SubCategoryDocument createOrUpdateInElasticsearch(SubCategoryResponse subCategoryResponse) {
//        SubCategoryDocument document = SubCategoryDocument.builder()
//                .id(subCategoryResponse.getId())
//                .title(subCategoryResponse.getTitle())
//                .description(subCategoryResponse.getDescription())
//                .coverImage(subCategoryResponse.getCoverImage())
//                .createdBy(subCategoryResponse.getCreatedBy())
//                .createdDate(subCategoryResponse.getCreatedDate())
//                .lastModifiedBy(subCategoryResponse.getLastModifiedBy())
//                .lastModifiedDate(subCategoryResponse.getLastModifiedDate())
//                .totalPosts(subCategoryResponse.getTotalPosts())
//                .build();
//        return subCategoryDocumentRepository.save(document);
//    }
//
//    @Override
//    public List<SubCategoryDocument> searchSubCategoriesInElasticsearch(String title, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return subCategoryDocumentRepository.findByTitleContainingIgnoreCase(title, pageable);
//    }
}
