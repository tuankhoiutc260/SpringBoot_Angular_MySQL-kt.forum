package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse create(RoleRequest roleRequest);

    RoleResponse findByRoleId(Integer roleId);

    List<RoleResponse> findAll();

    RoleResponse update(Integer roleId, RoleRequest roleRequest);

    void deleteByRoleId(Integer roleId);
}
