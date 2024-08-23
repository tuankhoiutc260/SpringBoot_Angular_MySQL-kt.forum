package com.tuankhoi.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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

    String imageUrl;

    String cloudinaryImageId;

    boolean active = true;

    String createdBy;

    LocalDateTime createdDate;

    String lastModifiedBy;

    LocalDateTime lastModifiedDate;

    RoleResponse role;
}
