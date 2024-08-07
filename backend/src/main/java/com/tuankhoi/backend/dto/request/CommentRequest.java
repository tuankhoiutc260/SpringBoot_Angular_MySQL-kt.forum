package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotBlank(message = "COMMENT_CONTENT_NOTBLANK")
    String content;

    @NotBlank(message = "COMMENT_POST_ID_NOTBLANK")
    String postId;

    @NotBlank(message = "COMMENT_PARENT_ID_NOTBLANK")
    Long parentCommentId;
}