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
public class PermissionRequest {
    @NotBlank(message = "PERMISSION_NAME_NOTBLANK")
    String name;

    @NotBlank(message = "PERMISSION_DESCRIPTION_NOTBLANK")
    String description;
}
