package com.tuankhoi.backend.repository;

import com.tuankhoi.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(String userId, String postId);
    Boolean existsByUserIdAndPostId(String userId, String postId);
    Long countByPostId(String postId);
}
