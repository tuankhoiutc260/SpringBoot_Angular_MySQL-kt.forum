package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("")
    public APIResponse<PermissionResponse> create(@RequestBody PermissionRequest permissionRequest) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.create(permissionRequest))
                .build();
    }

    @GetMapping("")
    public APIResponse<List<PermissionResponse>> findAll() {
        return APIResponse.<List<PermissionResponse>>builder()
                .result(permissionService.findAll())
                .build();
    }

    @PutMapping("/{id}")
    public APIResponse<PermissionResponse> update(@PathVariable Integer id, @RequestBody PermissionRequest permissionRequest) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.update(id, permissionRequest))
                .build();
    }

    @DeleteMapping("/{permissionId}")
    public APIResponse<Void> deleteByPermissionId(@PathVariable Integer permissionId) {
        permissionService.deleteByPermissionId(permissionId);
        return APIResponse.<Void>builder()
                .build();
    }
}
