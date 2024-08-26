package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Permission Controller")
public class PermissionController {
    PermissionService permissionService;

    @Operation(summary = "Create new permission", description = "Create a new permission.")
    @PostMapping("")
    public APIResponse<PermissionResponse> create(@RequestBody PermissionRequest permissionRequest) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.create(permissionRequest))
                .build();
    }

    @Operation(summary = "Get all permissions", description = "Retrieve a list of all permissions.")
    @GetMapping("")
    public APIResponse<Page<PermissionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size) {
        Page<PermissionResponse> permissionResponsePage = permissionService.getAll(page, size);
        return APIResponse.<Page<PermissionResponse>>builder()
                .result(permissionResponsePage)
                .build();
    }

    @Operation(summary = "Update permission", description = "Update a permission by its ID.")
    @PutMapping("/{permissionId}")
    public APIResponse<PermissionResponse> update(@PathVariable Integer permissionId,
                                                  @RequestBody PermissionRequest permissionRequest) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.update(permissionId, permissionRequest))
                .build();
    }

    @Operation(summary = "Delete permission", description = "Delete a permission by its ID.")
    @DeleteMapping("/{permissionId}")
    public APIResponse<Void> deleteById(@PathVariable Integer permissionId) {
        permissionService.deleteById(permissionId);
        return APIResponse.<Void>builder()
                .build();
    }
}
