package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    MultipartFile image;

    @NotBlank(message = "POST_TITLE_NOTBLANK")
    @Size(max = 255, message = "POST_TITLE_INVALID")
    String title;

    @NotBlank(message = "POST_CONTENT_NOTBLANK")
    String content;

    @NotEmpty(message = "POST_TAGS_NOTEMPTY")
    Set<String> tags = new HashSet<>();
}