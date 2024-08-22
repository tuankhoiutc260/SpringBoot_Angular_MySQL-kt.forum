package com.tuankhoi.backend.repository.Elasticsearch;

import com.tuankhoi.backend.dto.document.CategoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryElasticsearchRepository extends ElasticsearchRepository<CategoryDocument, String> {
}