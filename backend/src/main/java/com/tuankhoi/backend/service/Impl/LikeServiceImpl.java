package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.LikeMapper;
import com.tuankhoi.backend.model.entity.Like;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.LikeRepository;
import com.tuankhoi.backend.repository.Jpa.PostRepository;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import com.tuankhoi.backend.service.ILikeService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements ILikeService {
    private final LikeRepository LikeRepository;
    private final UserRepository UserRepository;
    private final PostRepository PostRepository;
    private final LikeMapper LikeMapper;

    public LikeServiceImpl(LikeRepository LikeRepository, UserRepository UserRepository, PostRepository PostRepository, LikeMapper LikeMapper) {
        this.LikeRepository = LikeRepository;
        this.UserRepository = UserRepository;
        this.PostRepository = PostRepository;
        this.LikeMapper = LikeMapper;
    }

    @Override
    public Long countLikes(LikeRequest likeRequest) {
        Post existingPost = PostRepository.findById(likeRequest.getPostId()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        return LikeRepository.countByPostId(existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public Boolean isLiked(LikeRequest likeRequest) {
        Post existingPost = PostRepository.findById(likeRequest.getPostId()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<User> existingUser = userRepository.findByUserName(authentication.getName());
        Optional<User> existingUser = UserRepository.findById(authentication.getName());

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        else if(existingUser.isEmpty())
            throw new AppException(ErrorCode.USER_NOTFOUND);

        return LikeRepository.existsByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public LikeResponse toggleLike(LikeRequest likeRequest) {
        Post existingPost = PostRepository.findById(likeRequest.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> existingUser = UserRepository.findById(authentication.getName());
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
        Optional<Like> isLiked = LikeRepository.findByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
        if (isLiked.isPresent()) {
            LikeRepository.delete(isLiked.get());
            return null;
        }
        Like like = new Like();
        like.setUser(existingUser.get());
        like.setPost(existingPost);
        return LikeMapper.toResponse(LikeRepository.save(like));
    }
}
