package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategoryRequest {
    @NotBlank(message = "SUB_CATEGORY_TITLE_NOTBLANK")
    String title;

    @NotBlank(message = "SUB_CATEGORY_DESCRIPTION_NOTBLANK")
    String description;

    MultipartFile coverImageFile;

    @NotNull(message = "CATEGORY_ID_NOTBLANK")
    String categoryId;
}