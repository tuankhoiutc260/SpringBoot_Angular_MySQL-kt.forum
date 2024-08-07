package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.dto.response.PostResponse;
import com.tuankhoi.backend.entity.Comment;
import com.tuankhoi.backend.entity.Like;
import com.tuankhoi.backend.entity.Post;
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
public class PostMapperImpl implements PostMapper {

    @Override
    public Post toPost(PostRequest postRequest) {
        if ( postRequest == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.title( postRequest.getTitle() );
        post.content( postRequest.getContent() );
        Set<String> set = postRequest.getTags();
        if ( set != null ) {
            post.tags( new LinkedHashSet<String>( set ) );
        }

        return post.build();
    }

    @Override
    public PostResponse toPostResponse(Post post) {
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

    @Override
    public void updatePost(Post post, PostRequest postRequest) {
        if ( postRequest == null ) {
            return;
        }

        post.setTitle( postRequest.getTitle() );
        post.setContent( postRequest.getContent() );
        if ( post.getTags() != null ) {
            Set<String> set = postRequest.getTags();
            if ( set != null ) {
                post.getTags().clear();
                post.getTags().addAll( set );
            }
            else {
                post.setTags( null );
            }
        }
        else {
            Set<String> set = postRequest.getTags();
            if ( set != null ) {
                post.setTags( new LinkedHashSet<String>( set ) );
            }
        }
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
}
