package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest permissionRequest);

    PermissionResponse findByName(String permissionName);

    List<PermissionResponse> findAll();

    PermissionResponse update(Integer permissionRequestID, PermissionRequest permissionRequest);

    void delete(Integer name);
}
