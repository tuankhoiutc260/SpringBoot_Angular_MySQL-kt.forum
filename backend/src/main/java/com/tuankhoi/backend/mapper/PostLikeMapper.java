package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.PostLikeRequest;
import com.tuankhoi.backend.dto.response.PostLikeResponse;
import com.tuankhoi.backend.model.entity.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostLikeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post.id", source = "postId")
    PostLike toPostLike(PostLikeRequest postLikeRequest);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    PostLikeResponse toPostLikeResponse(PostLike postLike);
}