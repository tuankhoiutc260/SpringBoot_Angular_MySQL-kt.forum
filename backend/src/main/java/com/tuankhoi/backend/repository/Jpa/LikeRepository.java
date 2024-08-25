package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndPostId(String userId, String postId);

    Boolean existsByUserIdAndPostId(String userId, String postId);

    Long countByPostId(String postId);
}
