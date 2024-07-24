package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "post.id", source = "postID")
    Comment toEntity(CommentRequest request);

    @Mapping(target = "postID", source = "post.id")
    CommentResponse toResponse(Comment comment);
}