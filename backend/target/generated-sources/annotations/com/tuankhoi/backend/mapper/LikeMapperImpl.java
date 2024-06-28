package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.LikeRequest;
import com.tuankhoi.backend.dto.response.LikeResponse;
import com.tuankhoi.backend.model.Like;
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

        like.createdDate( likeRequest.getCreatedDate() );

        return like.build();
    }

    @Override
    public LikeResponse toResponse(Like like) {
        if ( like == null ) {
            return null;
        }

        LikeResponse.LikeResponseBuilder likeResponse = LikeResponse.builder();

        likeResponse.createdDate( like.getCreatedDate() );
        likeResponse.user( like.getUser() );
        likeResponse.post( like.getPost() );

        return likeResponse.build();
    }
}
