package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedDateDesc(String postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :commentId ORDER BY c.createdDate ASC")
    List<Comment> findRepliesByCommentId(@Param("commentId") Long commentId, Pageable pageable);

    List<Comment> findAllByPostIdAndParentCommentIsNullOrParentCommentIdIsNotNullOrderByCreatedDateDesc(String postId, Pageable pageable);
}