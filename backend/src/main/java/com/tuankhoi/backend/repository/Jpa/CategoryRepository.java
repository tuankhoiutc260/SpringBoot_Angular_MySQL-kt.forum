package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("SELECT c FROM Category c JOIN c.subCategories sc WHERE sc.id = :subCategoryId")
    Optional<Category> findBySubCategoryId(@Param("subCategoryId") String subCategoryId);
}
