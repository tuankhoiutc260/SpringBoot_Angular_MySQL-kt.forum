//package com.tuankhoi.backend.repository.ElasticSearch;
//
//import com.tuankhoi.backend.model.entity.Post;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface IElasticsearchPostRepository extends ElasticsearchRepository<Post, String> {
//    Optional<Post> findByTitleContaining(String title);
//
//    List<Post> findBySubCategoryIdOrderByCreatedDateAsc(String subCategoryId, Pageable pageable);
//}
