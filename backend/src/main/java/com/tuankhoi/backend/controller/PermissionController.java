package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public APIResponse<Page<PermissionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size) {
        Page<PermissionResponse> permissionResponsePage = permissionService.getAll(page, size);
        return APIResponse.<Page<PermissionResponse>>builder()
                .result(permissionResponsePage)
                .build();
    }

    @PutMapping("/{permissionId}")
    public APIResponse<PermissionResponse> update(@PathVariable Integer permissionId,
                                                  @RequestBody PermissionRequest permissionRequest) {
        return APIResponse.<PermissionResponse>builder()
                .result(permissionService.update(permissionId, permissionRequest))
                .build();
    }

    @DeleteMapping("/{permissionId}")
    public APIResponse<Void> deleteById(@PathVariable Integer permissionId) {
        permissionService.deleteById(permissionId);
        return APIResponse.<Void>builder()
                .build();
    }
}
