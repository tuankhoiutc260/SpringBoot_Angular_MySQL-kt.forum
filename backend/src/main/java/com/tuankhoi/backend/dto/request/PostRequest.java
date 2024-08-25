package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    @NotBlank(message = "POST_TITLE_NOTBLANK")
    @Size(max = 255, message = "POST_TITLE_INVALID")
    String title;

    @NotBlank(message = "POST_CONTENT_NOTBLANK")
    String content;

    String subCategoryId;
}