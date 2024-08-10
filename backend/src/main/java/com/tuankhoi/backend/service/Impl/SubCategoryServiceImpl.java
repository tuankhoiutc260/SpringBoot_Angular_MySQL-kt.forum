package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.Category;
import com.tuankhoi.backend.entity.SubCategory;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.CategoryMapper;
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
                    .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            SubCategory newSubCategory = subCategoryMapper.toCategory(subCategoryRequest);
            newSubCategory.setCategory(existingCategory);
            SubCategory savedSubCategory = subCategoryRepository.save(newSubCategory);
            return subCategoryMapper.toResponse(savedSubCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Sub Category due to database constraint: " + e.getMessage(), e);
        }
    }

    @Override
    public SubCategoryResponse findById(String subCategoryID) {
        return null;
    }

    @Override
    public List<SubCategoryResponse> findByCategoryId(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOTFOUND));
        return subCategoryRepository.findByCategoryId(existingCategory.getId(), pageable)
                .stream()
                .map(subCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<SubCategoryResponse> findAll() {
        return subCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(subCategoryMapper::toResponse)
                .toList();
    }

    @Override
    public SubCategoryResponse update(String subCategoryRequestID, SubCategoryRequest subCategoryRequest) {
        return null;
    }

    @Override
    public void deleteById(String subCategoryRequest) {

    }
}
