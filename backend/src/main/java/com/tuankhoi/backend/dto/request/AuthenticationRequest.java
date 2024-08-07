package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "USER_NAME_NOTBLANK")
    private String userName;

    @NotBlank(message = "PASSWORD_NOTBLANK")
    private String password;
}
