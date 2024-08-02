package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest roleRequest);

    RoleResponse findById(Integer roleRequestId);

    List<RoleResponse> findAll();

    RoleResponse update(Integer roleRequestId, RoleRequest roleRequest);

    void deleteById(Integer roleRequestId);
}
