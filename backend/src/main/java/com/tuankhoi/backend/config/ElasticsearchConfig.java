package com.tuankhoi.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.tuankhoi.backend.repository.Elasticsearch")
public class ElasticsearchConfig {
}