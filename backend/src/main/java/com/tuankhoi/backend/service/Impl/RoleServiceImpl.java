package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.RoleMapper;
import com.tuankhoi.backend.entity.Permission;
import com.tuankhoi.backend.entity.Role;
import com.tuankhoi.backend.repository.PermissionRepository;
import com.tuankhoi.backend.repository.RoleRepository;
import com.tuankhoi.backend.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            throw new IllegalArgumentException("Failed to create Role due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse findById(Integer id) {
        return roleRepository.findById(id)
                .map(roleMapper::toRoleResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<RoleResponse> findAll() {
        return roleRepository.findAll(Sort.by("id"))
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse update(Integer id, RoleRequest roleRequest) {
        try {
            Role existingRole = roleRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = permissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            existingRole.setPermissions(permissions);
            roleMapper.updateRole(existingRole, roleRequest);
            return roleMapper.toRoleResponse(roleRepository.save(existingRole));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update role due to database constraint: " + e.getMessage());
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
            throw new IllegalArgumentException("Failed to delete role due to database constraint", e);
        }
    }
}
