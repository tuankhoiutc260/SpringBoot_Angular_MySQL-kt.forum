package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

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

    @NotBlank(message = "POST_DESCRIPTION_NOTBLANK")
    @Size(max = 300, message = "POST_DESCRIPTION_INVALID")
    String description;

    @NotBlank(message = "POST_CONTENT_NOTBLANK")
    String content;

    @NotBlank(message = "POST_SUBCATEGORYID_NOTBLANK")
    String subCategoryId;

    @NotEmpty(message = "POST_TAGS_NOTEMPTY")
    @Size(max = 10, message = "POST_TAGS_MAXSIZE")
    Set<@Size(max = 50, message = "POST_TAG_INVALID") String> tags;
}