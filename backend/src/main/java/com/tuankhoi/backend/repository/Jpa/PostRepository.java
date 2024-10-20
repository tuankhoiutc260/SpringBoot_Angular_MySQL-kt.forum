package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.dto.response.SubCategoryRankResponse;
import com.tuankhoi.backend.dto.response.UserRankResponse;
import com.tuankhoi.backend.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findBySubCategoryIdOrderByCreatedDateAsc(String subCategoryId, Pageable pageable);

    @Query(value = """
            WITH top_liked_posts AS
            (
                SELECT post_id
                FROM post_like
                GROUP BY post_id
                ORDER BY COUNT(*) DESC
                LIMIT 3
            )
            SELECT p.*
            FROM post p
            JOIN top_liked_posts t ON p.id = t.post_id;""", nativeQuery = true)
    List<Post> findTop6ByOrderByLikesDesc();

    @Query("SELECT p FROM Post p JOIN PostLike pl ON p.id = pl.post.id WHERE pl.user.id = :userId")
    Page<Post> findPostsLiked(String userId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN PostLike pl ON p.id = pl.post.id WHERE pl.user.id = :userId AND p.subCategory.id = :subCategoryId")
    Page<Post> findPostsLikedBySubCategoryId(String userId, String subCategoryId, Pageable pageable);

    Page<Post> findPostsByCreatedBy(String userId, Pageable pageable);

    @Query(value = """
            select * from post order by created_date desc limit 5;""", nativeQuery = true)
    List<Post> findTop5OrderByCreatedDateDesc();

    @Query(value = """
            SELECT created_by as userId, SUM(view_count) AS totalViews, COUNT(id) as totalPosts
            FROM post
            GROUP BY created_by
            ORDER BY totalViews DESC
            LIMIT 3;""", nativeQuery = true)
    List<UserRankResponse> findTheTop3MostInteractiveUsers();

    @Query(value = """
            SELECT sub_category_id, count(sub_category_id) as count_sub_category_used_in_post
            FROM post
            GROUP BY sub_category_id
            ORDER BY count_sub_category_used_in_post DESC
            LIMIT 10;""", nativeQuery = true)
    List<SubCategoryRankResponse> findTheTop10MostSubCategory();

    @Query(value = "SELECT * FROM post WHERE id != :postId ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<Post> find6RandomPost(String postId);
}
