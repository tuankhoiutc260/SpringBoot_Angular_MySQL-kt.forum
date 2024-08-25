package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentComment.id", source = "parentCommentId")
    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Comment toComment(CommentRequest request);

    @Mapping(target = "parentId", source = "parentComment.id")
    @Mapping(target = "postId", source = "post.id")
    CommentResponse toCommentResponse(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentComment.id", source = "parentCommentId")
    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateCommentFromRequest(CommentRequest commentRequest, @MappingTarget Comment comment);
}