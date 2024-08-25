package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest permissionRequest);

    PermissionResponse getByName(String permissionName);

    List<PermissionResponse> getAll();

    PermissionResponse update(Integer permissionRequestId, PermissionRequest permissionRequest);

    void deleteById(Integer permissionId);
}
