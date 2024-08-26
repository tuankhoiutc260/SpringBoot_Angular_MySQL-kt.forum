package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findBySubCategoryId(String subCategoryId, Pageable pageable);

//    @Query(value = """
//            WITH top_liked_posts AS
//            (
//                SELECT post_id
//                FROM likes
//                GROUP BY post_id
//                ORDER BY COUNT(*) DESC
//                LIMIT 10
//            )
//            SELECT p.*
//            FROM post p
//            JOIN top_liked_posts t ON p.id = t.post_id;""", nativeQuery = true)
//    List<Post> findTop10ByOrderByLikesDesc();
//
//    List<Post> findByCreatedBy(String createdBy);
//
//    @Query("SELECT p FROM Post p JOIN PostLike l ON p.id = l.post.id WHERE l.user.id = :userId")
//    List<Post> findPostsLiked(String userId);
//
//    List<Post> findBySubCategoryIdOrderByCreatedDateAsc(String subCategoryId, Pageable pageable);
//
//    Long countBySubCategoryId(String subCategoryId);
//
//    Long countByCreatedBy(String userId);
}
