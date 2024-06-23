package com.tuankhoi.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * For error codes, see {@link com.tuankhoi.backend.exception.ErrorCode}
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;

    @Email(message = "USER_EMAIL_INVALID")
    @NotBlank(message = "USER_EMAIL_NOTBLANK")
    private String email;

    @Size(min = 8, message = "USER_PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "USER_NAME_NOTBLANK")
    private String fullName;

    private boolean active = true;

    private LocalDateTime createdDate;

    private UUID createdBy;

    private LocalDateTime lastModifiedDate;

    private UUID lastModifiedBy;

    @NotNull(message = "ROLE_NOTNULL")
    private Integer role_id;
}
