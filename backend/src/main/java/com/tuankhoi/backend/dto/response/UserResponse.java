package com.tuankhoi.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;

    String userName;

    String password;

    String email;

    String fullName;

    String image;

    boolean active = true;

    LocalDateTime createdDate;

    String createdBy;

    LocalDateTime lastModifiedDate;

    String lastModifiedBy;

    RoleResponse role;
}
