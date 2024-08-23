package com.tuankhoi.backend.repository.Elasticsearch;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryElasticsearchRepository extends ElasticsearchRepository<SubCategoryDocument, String> {
}