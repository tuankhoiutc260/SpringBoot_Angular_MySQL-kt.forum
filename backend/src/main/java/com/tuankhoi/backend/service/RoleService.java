package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest roleRequest);

    RoleResponse findByID(Integer roleRequestID);

    List<RoleResponse> findAll();

    RoleResponse update(Integer roleRequestID, RoleRequest roleRequest);

    void deleteByID(Integer roleRequestID);
}
