package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "image", ignore = true)
    Post toPost(PostRequest postRequest);

    PostResponse toPostResponse(Post post);

    default PostResponse toPostResponseWithCountLikes(Post post) {
        PostResponse postResponse = toPostResponse(post);
        postResponse.setCountLikes(post.getLikes().size());
        return postResponse;
    }

    @Mapping(target = "image", ignore = true)
    void updatePost(@MappingTarget Post post, PostRequest postRequest);
}
