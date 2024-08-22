package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, String> {
    List<SubCategory> findByCategoryId(String categoryId, Pageable pageable);
    SubCategory findByTitle(String title);
}
