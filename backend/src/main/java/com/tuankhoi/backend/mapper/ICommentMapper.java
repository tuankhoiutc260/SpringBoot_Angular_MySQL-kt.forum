package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICommentMapper {
    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "parentComment.id", source = "parentCommentId")
    Comment toEntity(CommentRequest request);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "parentId", source = "parentComment.id")
//    @Mapping(target = "replies", source = "replies")
    CommentResponse toResponse(Comment comment);
}