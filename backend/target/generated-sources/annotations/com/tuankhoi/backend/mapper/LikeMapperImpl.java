package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.model.Like;
import com.tuankhoi.backend.model.Post;
import com.tuankhoi.backend.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class LikeMapperImpl implements LikeMapper {

    @Override
    public Like toLike(LikeRequest likeRequest) {
        if ( likeRequest == null ) {
            return null;
        }

        Like.LikeBuilder like = Like.builder();

        like.post( likeRequestToPost( likeRequest ) );

        return like.build();
    }

    @Override
    public LikeResponse toResponse(Like like) {
        if ( like == null ) {
            return null;
        }

        LikeResponse.LikeResponseBuilder likeResponse = LikeResponse.builder();

        likeResponse.postId( likePostId( like ) );
        likeResponse.userId( likeUserId( like ) );
        likeResponse.id( like.getId() );
        likeResponse.createdDate( like.getCreatedDate() );

        return likeResponse.build();
    }

    protected Post likeRequestToPost(LikeRequest likeRequest) {
        if ( likeRequest == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.id( likeRequest.getPostId() );

        return post.build();
    }

    private String likePostId(Like like) {
        if ( like == null ) {
            return null;
        }
        Post post = like.getPost();
        if ( post == null ) {
            return null;
        }
        String id = post.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String likeUserId(Like like) {
        if ( like == null ) {
            return null;
        }
        User user = like.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
