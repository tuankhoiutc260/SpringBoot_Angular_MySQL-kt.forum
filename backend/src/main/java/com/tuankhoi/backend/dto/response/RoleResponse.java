package com.tuankhoi.backend.dto.response;

import com.tuankhoi.backend.model.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse implements Serializable {
    Integer id;

    String name;

    String description;

    Set<PermissionResponse> permissions;
}
