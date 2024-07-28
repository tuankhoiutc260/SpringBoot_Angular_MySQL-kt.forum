package com.tuankhoi.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;

    String content;

    String postID;

    String parentID;

    List<CommentResponse> replies;

    String createdBy;

    LocalDateTime createdDate;

    LocalDateTime lastModifiedDate;
}
