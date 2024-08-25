package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest roleRequest);

    RoleResponse getById(Integer roleId);

    List<RoleResponse> getAll();

    RoleResponse update(Integer roleId, RoleRequest roleRequest);

    void deleteById(Integer roleId);
}
