package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    @Query(value = "SELECT p.*\n" +
            "FROM post p\n" +
            "JOIN (\n" +
            "    SELECT post_id\n" +
            "    FROM likes\n" +
            "    GROUP BY post_id\n" +
            "    ORDER BY COUNT(*) DESC\n" +
            "    LIMIT 10\n" +
            ") l ON p.id = l.post_id;", nativeQuery = true)
    List<Post> findTop10ByOrderByLikesDesc();

    List<Post> findByCreatedBy(String createdBy);
}
