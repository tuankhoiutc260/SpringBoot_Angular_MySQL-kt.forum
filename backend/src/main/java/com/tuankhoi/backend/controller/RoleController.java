package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping("/{roleId}")
    public APIResponse<RoleResponse> getById(@PathVariable int roleId) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.getById(roleId))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<RoleResponse>> findAll() {
        List<RoleResponse> roleResponseList = roleService.getAll();
        return APIResponse.<List<RoleResponse>>builder()
                .result(roleResponseList)
                .totalRecords(roleResponseList.size())
                .build();
    }

    @PostMapping
    public APIResponse<RoleResponse> create(@RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.create(roleRequest))
                .build();
    }

    @PutMapping("/{roleId}")
    public APIResponse<RoleResponse> update(@PathVariable int roleId, @RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.update(roleId, roleRequest))
                .build();
    }

    @DeleteMapping("/{roleId}")
    public APIResponse<Void> deleteById(@PathVariable int roleId) {
        roleService.deleteById(roleId);
        return APIResponse.<Void>builder()
                .build();
    }
}
