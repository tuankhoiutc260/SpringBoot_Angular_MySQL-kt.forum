package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedDateDesc(String postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :commentId ORDER BY c.createdDate ASC")
    Page<Comment> findRepliesByCommentId(@Param("commentId") Long commentId, Pageable pageable);

    Page<Comment> findAllByPostIdOrderByCreatedDateDesc(String postId, Pageable pageable);

//    List<Comment> findAllByPostIdAndParentCommentIsNullOrParentCommentIdIsNotNullOrderByCreatedDateDesc(String postId, Pageable pageable);
}