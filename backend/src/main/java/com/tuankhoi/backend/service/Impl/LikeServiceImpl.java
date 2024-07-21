package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.LikeMapper;
import com.tuankhoi.backend.model.Like;
import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.model.User;
import com.tuankhoi.backend.repository.LikeRepository;
import com.tuankhoi.backend.repository.PostRepository;
import com.tuankhoi.backend.repository.UserRepository;
import com.tuankhoi.backend.service.LikeService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;

    public LikeServiceImpl(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeMapper = likeMapper;
    }

    @Override
    public Long countLikes(LikeRequest likeRequest) {
        Post existingPost = postRepository.findById(likeRequest.getPostID()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        return likeRepository.countByPostId(existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public Boolean isLiked(LikeRequest likeRequest) {
        Post existingPost = postRepository.findById(likeRequest.getPostID()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<User> existingUser = userRepository.findByUserName(authentication.getName());
        Optional<User> existingUser = userRepository.findById(authentication.getName());

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        else if(existingUser.isEmpty())
            throw new AppException(ErrorCode.USER_NOTFOUND);

        return likeRepository.existsByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public LikeResponse toggleLike(LikeRequest likeRequest) {
        Post existingPost = postRepository.findById(likeRequest.getPostID())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> existingUser = userRepository.findById(authentication.getName());
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
        Optional<Like> isLiked = likeRepository.findByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
        if (isLiked.isPresent()) {
            likeRepository.delete(isLiked.get());
            return null;
        }
        Like like = new Like();
        like.setUser(existingUser.get());
        like.setPost(existingPost);
        return likeMapper.toResponse(likeRepository.save(like));
    }
}
