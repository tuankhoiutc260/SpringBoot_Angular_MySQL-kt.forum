package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.model.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "subCategory.id", source = "subCategoryId")
    Post toPost(PostRequest postRequest);

    PostResponse toPostResponse(Post post);

    default PostResponse toPostResponseWithCountLikes(Post post) {
        PostResponse postResponse = toPostResponse(post);
        postResponse.setCountLikes(post.getLikes() != null ? post.getLikes().size() : 0);
        postResponse.setTotalComments(post.getComments() != null ? post.getComments().size() : 0);
        return postResponse;
    }

    void updatePost(@MappingTarget Post post, PostRequest postRequest);
}
