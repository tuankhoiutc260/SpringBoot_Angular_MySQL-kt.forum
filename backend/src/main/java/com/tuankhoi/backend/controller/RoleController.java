package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role Controller")
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID.")
    @GetMapping("/{roleId}")
    public APIResponse<RoleResponse> getById(@PathVariable int roleId) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.getById(roleId))
                .build();
    }

    @Operation(summary = "Get all roles", description = "Retrieve a list of all roles.")
    @GetMapping("")
    public APIResponse<Page<RoleResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<RoleResponse> roleResponsePage = roleService.getAll(page, size);
        return APIResponse.<Page<RoleResponse>>builder()
                .result(roleResponsePage)
                .build();
    }

    @Operation(summary = "Create new role", description = "Create a new role.")
    @PostMapping
    public APIResponse<RoleResponse> create(@RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.create(roleRequest))
                .build();
    }

    @Operation(summary = "Update role", description = "Update a role by its ID.")
    @PutMapping("/{roleId}")
    public APIResponse<RoleResponse> update(@PathVariable int roleId,
                                            @RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.update(roleId, roleRequest))
                .build();
    }

    @Operation(summary = "Delete role", description = "Delete a role by its ID.")
    @DeleteMapping("/{roleId}")
    public APIResponse<Void> deleteById(@PathVariable int roleId) {
        roleService.deleteById(roleId);
        return APIResponse.<Void>builder()
                .build();
    }
}
