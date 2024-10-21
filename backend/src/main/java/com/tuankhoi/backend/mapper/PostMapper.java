package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.document.PostDocument;
import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.model.entity.Comment;
import com.tuankhoi.backend.model.entity.Post;
import com.tuankhoi.backend.model.entity.PostLike;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostMapper {
    // For JPA
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategory.id", source = "subCategoryId")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    Post toPost(PostRequest postRequest);

    @Mapping(target = "subCategoryId", source = "subCategory.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
    @Mapping(target = "likeCount", source = "postLikes", qualifiedByName = "countLikes")
    @Mapping(target = "commentCount", source = "comments", qualifiedByName = "countComments")
    PostResponse toPostResponse(Post post);

    // For Elasticsearch
    @Mapping(target = "subCategoryId", source = "subCategory.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "localDateTimeToDate")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "localDateTimeToDate")
    @Mapping(target = "likeCount", source = "postLikes", qualifiedByName = "countLikes")
    @Mapping(target = "commentCount", source = "comments", qualifiedByName = "countComments")
    PostDocument toPostDocument(Post post);

    @Mapping(target = "subCategory.id", source = "subCategoryId")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "lastModifiedBy.id", source = "lastModifiedBy")
    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "postLikes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    Post toPostFromDocument(PostDocument postDocument);

    @Mapping(target = "createdDate", source = "createdDate", qualifiedByName = "dateToLocalDateTime")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate", qualifiedByName = "dateToLocalDateTime")
    PostResponse toPostResponseFromDocument(PostDocument postDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategory.id", source = "subCategoryId")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    void updatePostFromRequest(PostRequest postRequest, @MappingTarget Post post);

    @Named("localDateTimeToDate")
    default Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC+7"));
        return Date.from(zonedDateTime.toInstant());
    }

    @Named("dateToLocalDateTime")
    default LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC+7"));
        return zonedDateTime.toLocalDateTime();
    }

    @Named("countLikes")
    default int countLikes(Set<PostLike> postLikes) {
        return postLikes != null ? postLikes.size() : 0;
    }

    @Named("countComments")
    default int countComments(Set<Comment> totalComments) {
        return totalComments != null ? totalComments.size() : 0;
    }
}
