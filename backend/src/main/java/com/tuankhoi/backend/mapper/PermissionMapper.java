package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.model.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);

    PermissionResponse toPermissionResponse(Permission permission);

    void updatePermission(@MappingTarget Permission permission, PermissionRequest permissionRequest);
}
