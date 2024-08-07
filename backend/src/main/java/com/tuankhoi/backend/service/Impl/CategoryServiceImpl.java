package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.CategoryRequest;
import com.tuankhoi.backend.dto.response.CategoryResponse;
import com.tuankhoi.backend.entity.Category;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.CategoryMapper;
import com.tuankhoi.backend.repository.CategoryRepository;
import com.tuankhoi.backend.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        try {
            Category newCategory = categoryMapper.toCategory(categoryRequest);
            Category savedCategory = categoryRepository.save(newCategory);
            return categoryMapper.toResponse(savedCategory);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Category due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse findById(String categoryID) {
        return categoryRepository.findById(categoryID)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse update(String categoryID, CategoryRequest categoryRequest) {
        try {
            Category existingCategory = categoryRepository.findById(categoryID)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            categoryMapper.updateCategory(existingCategory, categoryRequest);
            return categoryMapper.toResponse(categoryRepository.save(existingCategory));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update role due to database constraint: " + e.getMessage());
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(String categoryRequestID) {
        try {
            Category roleToDelete = categoryRepository.findById(categoryRequestID)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            categoryRepository.delete(roleToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete role due to database constraint", e);
        }
    }
}
