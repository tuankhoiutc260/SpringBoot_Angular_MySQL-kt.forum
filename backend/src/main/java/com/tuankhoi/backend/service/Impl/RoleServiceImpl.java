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
import com.tuankhoi.backend.service.IRoleService;
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
public class RoleServiceImpl implements IRoleService {
    RoleRepository RoleRepository;
    PermissionRepository PermissionRepository;
    RoleMapper RoleMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse create(RoleRequest roleRequest) {
        try {
            Role newRole = RoleMapper.toRole(roleRequest);
            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = PermissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            newRole.setPermissions(permissions);
            Role savedRole = RoleRepository.save(newRole);
            return RoleMapper.toRoleResponse(savedRole);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Role due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse findByRoleId(Integer roleId) {
        return RoleRepository.findById(roleId)
                .map(RoleMapper::toRoleResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<RoleResponse> findAll() {
        return RoleRepository.findAll(Sort.by("id"))
                .stream()
                .map(RoleMapper::toRoleResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse update(Integer roleId, RoleRequest roleRequest) {
        try {
            Role existingRole = RoleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = PermissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            existingRole.setPermissions(permissions);
            RoleMapper.updateRole(existingRole, roleRequest);
            return RoleMapper.toRoleResponse(RoleRepository.save(existingRole));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update role due to database constraint: " + e.getMessage());
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteByRoleId(Integer roleId) {
        try {
            Role roleToDelete = RoleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
            RoleRepository.delete(roleToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete role due to database constraint", e);
        }
    }
}
