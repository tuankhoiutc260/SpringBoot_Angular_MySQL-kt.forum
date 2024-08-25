package com.tuankhoi.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserRequest {
    @Size(min = 8, message = "USER_NAME_INVALID")
    @NotBlank(message = "USER_NAME_NOTBLANK")
    String userName;

    @Size(min = 8, message = "USER_PASSWORD_INVALID")
    @NotBlank(message = "USER_PASSWORD_NOTBLANK")
    String password;

    @Email(message = "USER_EMAIL_INVALID")
    @NotBlank(message = "USER_EMAIL_NOTBLANK")
    String email;

    String fullName;

    MultipartFile imageFile;

    @Builder.Default
    boolean active = true;

    Integer roleId;
}
