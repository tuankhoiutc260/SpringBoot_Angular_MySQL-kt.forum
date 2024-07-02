package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
    @Mapping(source = "postID", target = "post.id")
    Like toLike(LikeRequest likeRequest);

    @Mapping(source = "post.id", target = "postID")
    @Mapping(source = "user.id", target = "userID")
    LikeResponse toResponse(Like like);
}