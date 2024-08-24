package com.tuankhoi.backend.repository.Elasticsearch;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import com.tuankhoi.backend.model.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryElasticsearchRepository extends ElasticsearchRepository<SubCategoryDocument, String> {
    List<SubCategoryDocument> findByCategoryId(String categoryId, Pageable pageable);

}