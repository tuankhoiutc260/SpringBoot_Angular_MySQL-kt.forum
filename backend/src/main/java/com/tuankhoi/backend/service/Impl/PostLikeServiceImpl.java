package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PostLikeRequest;
import com.tuankhoi.backend.dto.response.PostLikeResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PostLikeMapper;
import com.tuankhoi.backend.model.entity.PostLike;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.LikeRepository;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import com.tuankhoi.backend.service.PostLikeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostLikeServiceImpl implements PostLikeService {
    LikeRepository likeRepository;
    UserRepository userRepository;
    PostRepository postRepository;
    PostLikeMapper postLikeMapper;

    @Cacheable(value = "postLikeCount", key = "#postLikeRequest.postId", unless = "#result == 0")
    @Override
    public Long countLikes(PostLikeRequest postLikeRequest) {
        Post existingPost = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
        return likeRepository.countByPostId(existingPost.getId());
    }

    //    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Cacheable(value = "postLikeStatus", key = "#postLikeRequest.postId + '_' + @auth.getAuthentication().name")
    @Override
    public Boolean isLiked(PostLikeRequest postLikeRequest) {
        Post existingPost = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> existingUser = userRepository.findById(authentication.getName());

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } else if (existingUser.isEmpty())
            throw new AppException(ErrorCode.USER_NOTFOUND);

        return likeRepository.existsByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
    }

    //    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Caching(evict = {
            @CacheEvict(value = "postLikeCount", key = "#postLikeRequest.postId"),
            @CacheEvict(value = "postLikeStatus", key = "#postLikeRequest.postId + '_' + #userId"),
            @CacheEvict(value = "post", key = "#postLikeRequest.postId"),
            @CacheEvict(value = "posts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true)
    })
    @Override
    public PostLikeResponse toggleLike(PostLikeRequest postLikeRequest, String userId) {
        Post existingPost = postRepository.findById(postLikeRequest.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));

        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }

        Optional<PostLike> isLiked = likeRepository.findByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
        if (isLiked.isPresent()) {
            likeRepository.delete(isLiked.get());
            return null;
        }

        PostLike postLike = new PostLike();
        postLike.setUser(existingUser.get());
        postLike.setPost(existingPost);
        return postLikeMapper.toPostLikeResponse(likeRepository.save(postLike));
    }
}
