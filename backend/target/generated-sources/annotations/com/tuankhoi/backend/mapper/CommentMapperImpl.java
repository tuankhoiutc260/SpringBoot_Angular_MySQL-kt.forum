package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.CommentRequest;
import com.tuankhoi.backend.dto.response.CommentResponse;
import com.tuankhoi.backend.entity.Comment;
import com.tuankhoi.backend.entity.Post;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toEntity(CommentRequest request) {
        if ( request == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.post( commentRequestToPost( request ) );
        comment.parentComment( commentRequestToComment( request ) );
        comment.content( request.getContent() );

        return comment.build();
    }

    @Override
    public CommentResponse toResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse.CommentResponseBuilder commentResponse = CommentResponse.builder();

        commentResponse.postId( commentPostId( comment ) );
        Long id1 = commentParentCommentId( comment );
        if ( id1 != null ) {
            commentResponse.parentId( String.valueOf( id1 ) );
        }
        commentResponse.replies( commentListToCommentResponseList( comment.getReplies() ) );
        commentResponse.id( comment.getId() );
        commentResponse.content( comment.getContent() );
        commentResponse.createdBy( comment.getCreatedBy() );
        commentResponse.createdDate( comment.getCreatedDate() );
        commentResponse.lastModifiedDate( comment.getLastModifiedDate() );

        return commentResponse.build();
    }

    protected Post commentRequestToPost(CommentRequest commentRequest) {
        if ( commentRequest == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.id( commentRequest.getPostId() );

        return post.build();
    }

    protected Comment commentRequestToComment(CommentRequest commentRequest) {
        if ( commentRequest == null ) {
            return null;
        }

        Comment.CommentBuilder comment = Comment.builder();

        comment.id( commentRequest.getParentCommentId() );

        return comment.build();
    }

    private String commentPostId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Post post = comment.getPost();
        if ( post == null ) {
            return null;
        }
        String id = post.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long commentParentCommentId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Comment parentComment = comment.getParentComment();
        if ( parentComment == null ) {
            return null;
        }
        Long id = parentComment.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<CommentResponse> commentListToCommentResponseList(List<Comment> list) {
        if ( list == null ) {
            return null;
        }

        List<CommentResponse> list1 = new ArrayList<CommentResponse>( list.size() );
        for ( Comment comment : list ) {
            list1.add( toResponse( comment ) );
        }

        return list1;
    }
}
