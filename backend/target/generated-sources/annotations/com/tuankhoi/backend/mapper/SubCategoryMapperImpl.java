package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.SubCategoryRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.dto.response.SubCategoryResponse;
import com.tuankhoi.backend.entity.Comment;
import com.tuankhoi.backend.entity.Like;
import com.tuankhoi.backend.entity.Post;
import com.tuankhoi.backend.entity.SubCategory;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class SubCategoryMapperImpl implements SubCategoryMapper {

    @Override
    public SubCategory toCategory(SubCategoryRequest subCategoryRequest) {
        if ( subCategoryRequest == null ) {
            return null;
        }

        SubCategory.SubCategoryBuilder subCategory = SubCategory.builder();

        subCategory.title( subCategoryRequest.getTitle() );
        subCategory.description( subCategoryRequest.getDescription() );

        return subCategory.build();
    }

    @Override
    public SubCategoryResponse toResponse(SubCategory subCategory) {
        if ( subCategory == null ) {
            return null;
        }

        SubCategoryResponse.SubCategoryResponseBuilder subCategoryResponse = SubCategoryResponse.builder();

        subCategoryResponse.title( subCategory.getTitle() );
        subCategoryResponse.description( subCategory.getDescription() );
        subCategoryResponse.coverImage( subCategory.getCoverImage() );
        subCategoryResponse.posts( postListToPostResponseList( subCategory.getPosts() ) );
        subCategoryResponse.createdBy( subCategory.getCreatedBy() );
        subCategoryResponse.createdDate( subCategory.getCreatedDate() );
        subCategoryResponse.lastModifiedBy( subCategory.getLastModifiedBy() );
        subCategoryResponse.lastModifiedDate( subCategory.getLastModifiedDate() );

        return subCategoryResponse.build();
    }

    protected LikeResponse likeToLikeResponse(Like like) {
        if ( like == null ) {
            return null;
        }

        LikeResponse.LikeResponseBuilder likeResponse = LikeResponse.builder();

        likeResponse.id( like.getId() );
        likeResponse.createdDate( like.getCreatedDate() );

        return likeResponse.build();
    }

    protected Set<LikeResponse> likeSetToLikeResponseSet(Set<Like> set) {
        if ( set == null ) {
            return null;
        }

        Set<LikeResponse> set1 = new LinkedHashSet<LikeResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Like like : set ) {
            set1.add( likeToLikeResponse( like ) );
        }

        return set1;
    }

    protected List<CommentResponse> commentListToCommentResponseList(List<Comment> list) {
        if ( list == null ) {
            return null;
        }

        List<CommentResponse> list1 = new ArrayList<CommentResponse>( list.size() );
        for ( Comment comment : list ) {
            list1.add( commentToCommentResponse( comment ) );
        }

        return list1;
    }

    protected CommentResponse commentToCommentResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse.CommentResponseBuilder commentResponse = CommentResponse.builder();

        commentResponse.id( comment.getId() );
        commentResponse.content( comment.getContent() );
        commentResponse.replies( commentListToCommentResponseList( comment.getReplies() ) );
        commentResponse.createdBy( comment.getCreatedBy() );
        commentResponse.createdDate( comment.getCreatedDate() );
        commentResponse.lastModifiedDate( comment.getLastModifiedDate() );

        return commentResponse.build();
    }

    protected Set<CommentResponse> commentSetToCommentResponseSet(Set<Comment> set) {
        if ( set == null ) {
            return null;
        }

        Set<CommentResponse> set1 = new LinkedHashSet<CommentResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Comment comment : set ) {
            set1.add( commentToCommentResponse( comment ) );
        }

        return set1;
    }

    protected PostResponse postToPostResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        PostResponse.PostResponseBuilder postResponse = PostResponse.builder();

        postResponse.id( post.getId() );
        postResponse.image( post.getImage() );
        postResponse.title( post.getTitle() );
        postResponse.content( post.getContent() );
        Set<String> set = post.getTags();
        if ( set != null ) {
            postResponse.tags( new LinkedHashSet<String>( set ) );
        }
        postResponse.createdDate( post.getCreatedDate() );
        postResponse.createdBy( post.getCreatedBy() );
        postResponse.lastModifiedDate( post.getLastModifiedDate() );
        postResponse.lastModifiedBy( post.getLastModifiedBy() );
        postResponse.likes( likeSetToLikeResponseSet( post.getLikes() ) );
        postResponse.comments( commentSetToCommentResponseSet( post.getComments() ) );

        return postResponse.build();
    }

    protected List<PostResponse> postListToPostResponseList(List<Post> list) {
        if ( list == null ) {
            return null;
        }

        List<PostResponse> list1 = new ArrayList<PostResponse>( list.size() );
        for ( Post post : list ) {
            list1.add( postToPostResponse( post ) );
        }

        return list1;
    }
}
