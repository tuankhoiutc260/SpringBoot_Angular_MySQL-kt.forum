package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    PermissionResponse create(PermissionRequest permissionRequest);

    PermissionResponse findByName(String permissionName);

    List<PermissionResponse> findAll();

    PermissionResponse update(Integer permissionRequestId, PermissionRequest permissionRequest);

    void deleteByPermissionId(Integer permissionId);
}
