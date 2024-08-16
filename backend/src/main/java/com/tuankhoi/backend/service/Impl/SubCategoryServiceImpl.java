package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.Category;
import com.tuankhoi.backend.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.SubCategoryMapper;
import com.tuankhoi.backend.repository.CategoryRepository;
import com.tuankhoi.backend.repository.SubCategoryRepository;
import com.tuankhoi.backend.service.SubCategoryService;
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
public class SubCategoryServiceImpl implements SubCategoryService {
    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;
    SubCategoryMapper subCategoryMapper;

    @Override
    public SubCategoryResponse create(SubCategoryRequest subCategoryRequest) {
        try {
            Category existingCategory = categoryRepository.findById(subCategoryRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            SubCategory newSubCategory = subCategoryMapper.toCategory(subCategoryRequest);
            newSubCategory.setCategory(existingCategory);
            SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);
            return subCategoryMapper.toSubCategoryResponseWithTotalPosts(savedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Sub Category due to database constraint: " + e.getMessage(), e);
        }
    }

    @Override
    public SubCategoryResponse findBySubCategoryId(String subCategoryID) {
        return subCategoryRepository.findById(subCategoryID)
                .map(subCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
    }

    @Override
    public List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
        return subCategoryRepository.findByCategoryId(existingCategory.getId(), pageable)
                .stream()
                .map(subCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .toList();
    }

    @Override
    public List<SubCategoryResponse> findAll() {
        return subCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(subCategoryMapper::toSubCategoryResponseWithTotalPosts)
                .toList();
    }

    @Override
    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
        try {
            SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryRequestID)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
            subCategoryMapper.updateSubCategory(existingSubCategory, subCategoryRequest);
            return subCategoryMapper.toSubCategoryResponseWithTotalPosts(subCategoryRepository.save(existingSubCategory));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update Sub Category due to database constraint: " + e.getMessage());
        }
    }

    @Override
    public void deleteBySubCategoryId(String subCategoryRequestId) {
        try {
            SubCategory existingSubCategory = subCategoryRepository.findById(subCategoryRequestId)
                    .orElseThrow(() -> new AppException(ErrorCode.SUB_CATEGORY_NOTFOUND));
            subCategoryRepository.deleteById(existingSubCategory.getId());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete Sub Category due to database constraint: " + e.getMessage());
        }
    }
}
