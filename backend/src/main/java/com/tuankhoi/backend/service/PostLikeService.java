package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PostLikeRequest;
import com.tuankhoi.backend.dto.response.PostLikeResponse;

public interface PostLikeService {
    Long countLikes(PostLikeRequest request);

    Boolean isLiked(PostLikeRequest postLikeRequest);

    PostLikeResponse toggleLike(PostLikeRequest postLikeRequest);
}
