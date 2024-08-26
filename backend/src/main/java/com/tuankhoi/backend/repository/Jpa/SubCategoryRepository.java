package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    Page<SubCategory> findByCategoryId(String categoryId, Pageable pageable);

    SubCategory findByTitle(String title);
}
