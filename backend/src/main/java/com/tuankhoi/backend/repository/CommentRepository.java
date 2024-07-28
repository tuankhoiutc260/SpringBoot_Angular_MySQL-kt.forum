package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    List<Comment> findByPostIdOrderByCreatedDateDesc(String postId);
//
//    List<Comment> findByParentId(String parentID);

//    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
//    List<Comment> findByParentCommentIdOrderByCreatedAtDesc(Long parentId, Pageable pageable);


    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedDateDesc(String postID, Pageable pageable);
    List<Comment> findByParentCommentIdOrderByCreatedDateDesc(Long parentID, Pageable pageable);


}