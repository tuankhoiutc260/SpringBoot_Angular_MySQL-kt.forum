package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
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
public class UserRequest {
    @Size(min = 8, message = "USER_NAME_INVALID")
    @NotBlank(message = "USER_NAME_NOTBLANK")
    String userName;

    @Size(min = 8, message = "USER_PASSWORD_INVALID")
    String password;

    @Email(message = "USER_EMAIL_INVALID")
    String email;

    String fullName;

    boolean active = true;

    Integer role;
}
