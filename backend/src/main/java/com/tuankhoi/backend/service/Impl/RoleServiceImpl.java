package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.RoleMapper;
import com.tuankhoi.backend.model.entity.Permission;
import com.tuankhoi.backend.model.entity.Role;
import com.tuankhoi.backend.repository.Jpa.PermissionRepository;
import com.tuankhoi.backend.repository.Jpa.RoleRepository;
import com.tuankhoi.backend.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse create(RoleRequest roleRequest) {
        try {
            Role newRole = roleMapper.toRole(roleRequest);

            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = permissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            newRole.setPermissions(permissions);

            Role savedRole = roleRepository.save(newRole);

            return roleMapper.toRoleResponse(savedRole);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Create Role due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Create Role", e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse getById(Integer roleId) {
        return roleRepository.findById(roleId)
                .map(roleMapper::toRoleResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll(Sort.by("id"))
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse update(Integer roleId, RoleRequest roleRequest) {
        try {
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));

            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = permissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            existingRole.setPermissions(permissions);

            roleMapper.updateRoleFromRequest(roleRequest, existingRole);

            return roleMapper.toRoleResponse(roleRepository.save(existingRole));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Update Role due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to Update Role", e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(Integer roleId) {
        try {
            Role roleToDelete = roleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));

            roleRepository.delete(roleToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Delete Role due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Delete Role", e);
        }
    }
}
