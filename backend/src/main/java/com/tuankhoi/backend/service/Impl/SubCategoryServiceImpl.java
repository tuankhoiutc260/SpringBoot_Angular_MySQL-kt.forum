package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.Category;
import com.tuankhoi.backend.model.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.ISubCategoryMapper;
//import com.tuankhoi.backend.repository.ElasticSearch.ISubCategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Elasticsearch.ISubCategoryElasticsearchRepository;
import com.tuankhoi.backend.repository.Jpa.CategoryRepository;
import com.tuankhoi.backend.repository.Jpa.ISubCategoryRepository;
import com.tuankhoi.backend.service.ISubCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
public class SubCategoryServiceImpl implements ISubCategoryService {
    CategoryRepository CategoryRepository;
    ISubCategoryRepository ISubCategoryRepository;
    ISubCategoryMapper ISubCategoryMapper;
    ISubCategoryElasticsearchRepository iSubCategoryElasticsearchRepository;
    @Override
    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
        try {
            Category existingCategory = CategoryRepository.findById(subCategoryRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            SubCategory newSubCategory = ISubCategoryMapper.toSubCategory(subCategoryRequest);
            newSubCategory.setCategory(existingCategory);
            SubCategory savedSubCategory = ISubCategoryRepository.save(newSubCategory);

            // Tạo và lưu DTO cho Elasticsearch
            SubCategoryDocument elasticsearchDTO = new SubCategoryDocument();
            elasticsearchDTO.setId(savedSubCategory.getId());
            elasticsearchDTO.setTitle(savedSubCategory.getTitle());
            elasticsearchDTO.setDescription(savedSubCategory.getDescription());
            elasticsearchDTO.setCoverImage(savedSubCategory.getCoverImage());
            elasticsearchDTO.setCategoryId(existingCategory.getId());
            iSubCategoryElasticsearchRepository.save(elasticsearchDTO);

            return ISubCategoryMapper.toSubCategoryResponseWithTotalPosts(savedSubCategory);
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

    @Override
    public SubCategoryResponse findBySubCategoryId(String subCategoryID) {
        return ISubCategoryRepository.findById(subCategoryID)
                .map(ISubCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Override
    public List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Category existingCategory = CategoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
        return ISubCategoryRepository.findByCategoryId(existingCategory.getId(), pageable)
                .stream()
                .map(ISubCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .toList();
    }

    @Override
    public List<SubCategoryResponse> findAll() {
        return ISubCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(ISubCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .toList();
    }

    @Override
    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
        try {
            SubCategory existingSubCategory = ISubCategoryRepository.findById(subCategoryRequestID)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
            ISubCategoryMapper.updateSubCategory(existingSubCategory, subCategoryRequest);
            return ISubCategoryMapper.toSubCategoryResponseWithTotalPosts(ISubCategoryRepository.save(existingSubCategory));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update Sub Category due to database constraint: " + e.getMessage());
        }
    }

    @Override
    public void deleteBySubCategoryId(String subCategoryRequestId) {
        try {
            SubCategory existingSubCategory = ISubCategoryRepository.findById(subCategoryRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
            ISubCategoryRepository.deleteById(existingSubCategory.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete Sub Category due to database constraint: " + e.getMessage());
        }
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
