package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    List<SubCategory> findByCategoryId(String categoryId, Pageable pageable);
}