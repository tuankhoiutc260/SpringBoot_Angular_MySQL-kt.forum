package com.tuankhoi.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private UUID id;

    @NotBlank(message = "POST_TITLE_NOTBLANK")
    @Size(max = 255, message = "POST_TITLE_INVALID")
    private String title;

    @NotBlank(message = "POST_CONTENT_NOTBLANK")
    private String content;

    @NotEmpty(message = "POST_TAGS_NOTEMPTY")
    private Set<String> tags = new HashSet<>();
    private LocalDateTime createdDate;
    private UUID createdBy;
    private LocalDateTime lastModifiedDate;
    private UUID lastModifiedBy;
}
