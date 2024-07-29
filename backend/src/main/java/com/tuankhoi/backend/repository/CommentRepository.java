package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedDateDesc(String postID, Pageable pageable);

    List<Comment> findByParentCommentIdOrderByCreatedDateDesc(Long parentCommentID, Pageable pageable);

    List<Comment> findAllByParentCommentIdOrderByCreatedDateDesc(Long parentCommentId);
}