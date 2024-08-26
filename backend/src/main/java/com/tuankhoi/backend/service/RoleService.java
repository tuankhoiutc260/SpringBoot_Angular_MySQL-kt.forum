package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;
import org.springframework.data.domain.Page;

public interface RoleService {
    RoleResponse create(RoleRequest roleRequest);

    RoleResponse getById(Integer roleId);

    Page<RoleResponse> getAll(int page, int size);

    RoleResponse update(Integer roleId, RoleRequest roleRequest);

    void deleteById(Integer roleId);
}
