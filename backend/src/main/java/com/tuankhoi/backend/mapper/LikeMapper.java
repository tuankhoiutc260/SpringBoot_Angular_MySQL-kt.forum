package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.model.Like;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    Like toLike(LikeRequest likeRequest);

    LikeResponse toResponse(Like like);
}