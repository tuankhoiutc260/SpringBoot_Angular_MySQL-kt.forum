package com.tuankhoi.backend.repository.Elasticsearch;

import com.tuankhoi.backend.dto.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, String> {
}