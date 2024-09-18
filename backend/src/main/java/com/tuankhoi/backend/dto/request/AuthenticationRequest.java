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
public class AuthenticationRequest {
    @NotBlank(message = "USER_NAME_NOTBLANK")
    String userName;

    @NotBlank(message = "PASSWORD_NOTBLANK")
    String password;

    boolean isRememberMe;
}
