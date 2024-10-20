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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @Size(min = 8, message = "USER_PASSWORD_INVALID")
    @NotBlank(message = "USER_PASSWORD_NOTBLANK")
    String currentPassword;

    @Size(min = 8, message = "USER_PASSWORD_INVALID")
    @NotBlank(message = "USER_PASSWORD_NOTBLANK")
    String newPassword;
}
