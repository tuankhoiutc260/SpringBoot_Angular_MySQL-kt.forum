package com.tuankhoi.backend.repository.Elasticsearch;

import com.tuankhoi.backend.dto.document.SubCategoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ISubCategoryElasticsearchRepository extends ElasticsearchRepository<SubCategoryDocument, String> {
}