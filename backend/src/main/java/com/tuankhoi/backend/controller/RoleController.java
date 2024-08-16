package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    RoleService roleService;

    @GetMapping("/{roleId}")
    public APIResponse<RoleResponse> findByRoleId(@PathVariable int roleId) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.findByRoleId(roleId))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<RoleResponse>> findAll() {
        return APIResponse.<List<RoleResponse>>builder()
                .result(roleService.findAll())
                .build();
    }

    @PostMapping
    public APIResponse<RoleResponse> create(@RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.create(roleRequest))
                .build();
    }

    @PutMapping("/{id}")
    public APIResponse<RoleResponse> update(@PathVariable int roleId, @RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.update(roleId, roleRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> deleteByRoleId(@PathVariable int roleId) {
        roleService.deleteByRoleId(roleId);
        return APIResponse.<Void>builder()
                .build();
    }
}
