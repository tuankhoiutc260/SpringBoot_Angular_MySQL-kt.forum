package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import org.springframework.data.domain.Page;

public interface PermissionService {
    PermissionResponse create(PermissionRequest permissionRequest);

    PermissionResponse getByName(String permissionName);

    Page<PermissionResponse> getAll(int page, int size);

    PermissionResponse update(Integer permissionRequestId, PermissionRequest permissionRequest);

    void deleteById(Integer permissionId);
}
