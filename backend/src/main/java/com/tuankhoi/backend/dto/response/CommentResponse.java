package com.tuankhoi.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;

    String content;

    String postID;

    LocalDateTime createdDate;

    String createdBy;

    LocalDateTime lastModifiedDate;

    String lastModifiedBy;
}