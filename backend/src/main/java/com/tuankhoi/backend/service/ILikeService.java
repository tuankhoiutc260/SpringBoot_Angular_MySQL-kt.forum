package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;

public interface ILikeService {
    Long countLikes(LikeRequest request);

    Boolean isLiked(LikeRequest likeRequest);

    LikeResponse toggleLike(LikeRequest likeRequest);
}
