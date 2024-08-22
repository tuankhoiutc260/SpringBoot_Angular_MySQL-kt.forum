package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.ILikeMapper;
import com.tuankhoi.backend.model.entity.Like;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.ILikeRepository;
import com.tuankhoi.backend.repository.Jpa.IPostRepository;
import com.tuankhoi.backend.repository.Jpa.IUserRepository;
import com.tuankhoi.backend.service.ILikeService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements ILikeService {
    private final ILikeRepository ILikeRepository;
    private final IUserRepository IUserRepository;
    private final IPostRepository IPostRepository;
    private final ILikeMapper ILikeMapper;

    public LikeServiceImpl(ILikeRepository ILikeRepository, IUserRepository IUserRepository, IPostRepository IPostRepository, ILikeMapper ILikeMapper) {
        this.ILikeRepository = ILikeRepository;
        this.IUserRepository = IUserRepository;
        this.IPostRepository = IPostRepository;
        this.ILikeMapper = ILikeMapper;
    }

    @Override
    public Long countLikes(LikeRequest likeRequest) {
        Post existingPost = IPostRepository.findById(likeRequest.getPostId()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        return ILikeRepository.countByPostId(existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public Boolean isLiked(LikeRequest likeRequest) {
        Post existingPost = IPostRepository.findById(likeRequest.getPostId()).orElseThrow(()->new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Optional<User> existingUser = userRepository.findByUserName(authentication.getName());
        Optional<User> existingUser = IUserRepository.findById(authentication.getName());

        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        else if(existingUser.isEmpty())
            throw new AppException(ErrorCode.USER_NOTFOUND);

        return ILikeRepository.existsByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('STAFF')")
    @Override
    public LikeResponse toggleLike(LikeRequest likeRequest) {
        Post existingPost = IPostRepository.findById(likeRequest.getPostId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOTFOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> existingUser = IUserRepository.findById(authentication.getName());
        if (existingUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
        Optional<Like> isLiked = ILikeRepository.findByUserIdAndPostId(existingUser.get().getId(), existingPost.getId());
        if (isLiked.isPresent()) {
            ILikeRepository.delete(isLiked.get());
            return null;
        }
        Like like = new Like();
        like.setUser(existingUser.get());
        like.setPost(existingPost);
        return ILikeMapper.toResponse(ILikeRepository.save(like));
    }
}
