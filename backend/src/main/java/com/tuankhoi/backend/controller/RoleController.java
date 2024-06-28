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
@RequestMapping("/api/v1/role")
public class RoleController {
    RoleService roleService;

    @GetMapping("/{id}")
    public APIResponse<RoleResponse> findByID(@PathVariable int id) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.findByID(id))
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
    public APIResponse<RoleResponse> update(@PathVariable int id, @RequestBody RoleRequest roleRequest) {
        return APIResponse.<RoleResponse>builder()
                .result(roleService.update(id, roleRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> delete(@PathVariable int id) {
        roleService.deleteByID(id);
        return APIResponse.<Void>builder()
                .build();
    }
}
