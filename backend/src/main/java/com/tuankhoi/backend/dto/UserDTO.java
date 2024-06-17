package com.tuankhoi.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private boolean active = true;
    private LocalDateTime createdDate;
    private UUID createdBy;
    private LocalDateTime lastModifiedDate;
    private UUID lastModifiedBy;
    private Integer role_id;
}
