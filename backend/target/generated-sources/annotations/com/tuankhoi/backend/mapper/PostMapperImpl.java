package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.PostDTO;
import com.tuankhoi.backend.model.Post;
import java.util.LinkedHashSet;
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
    public PostDTO mapToDTO(Post post) {
        if ( post == null ) {
            return null;
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setId( post.getId() );
        postDTO.setTitle( post.getTitle() );
        postDTO.setContent( post.getContent() );
        Set<String> set = post.getTags();
        if ( set != null ) {
            postDTO.setTags( new LinkedHashSet<String>( set ) );
        }
        postDTO.setCreatedDate( post.getCreatedDate() );
        postDTO.setCreatedBy( post.getCreatedBy() );
        postDTO.setLastModifiedDate( post.getLastModifiedDate() );
        postDTO.setLastModifiedBy( post.getLastModifiedBy() );

        return postDTO;
    }

    @Override
    public Post mapToEntity(PostDTO postDTO) {
        if ( postDTO == null ) {
            return null;
        }

        Post post = new Post();

        post.setId( postDTO.getId() );
        post.setTitle( postDTO.getTitle() );
        post.setContent( postDTO.getContent() );
        Set<String> set = postDTO.getTags();
        if ( set != null ) {
            post.setTags( new LinkedHashSet<String>( set ) );
        }
        post.setCreatedDate( postDTO.getCreatedDate() );
        post.setCreatedBy( postDTO.getCreatedBy() );
        post.setLastModifiedDate( postDTO.getLastModifiedDate() );
        post.setLastModifiedBy( postDTO.getLastModifiedBy() );

        return post;
    }

    @Override
    public void updatePostFromDTO(PostDTO postDTO, Post post) {
        if ( postDTO == null ) {
            return;
        }

        post.setTitle( postDTO.getTitle() );
        post.setContent( postDTO.getContent() );
        if ( post.getTags() != null ) {
            Set<String> set = postDTO.getTags();
            if ( set != null ) {
                post.getTags().clear();
                post.getTags().addAll( set );
            }
            else {
                post.setTags( null );
            }
        }
        else {
            Set<String> set = postDTO.getTags();
            if ( set != null ) {
                post.setTags( new LinkedHashSet<String>( set ) );
            }
        }
        post.setCreatedDate( postDTO.getCreatedDate() );
        post.setCreatedBy( postDTO.getCreatedBy() );
        post.setLastModifiedDate( postDTO.getLastModifiedDate() );
        post.setLastModifiedBy( postDTO.getLastModifiedBy() );
    }
}
