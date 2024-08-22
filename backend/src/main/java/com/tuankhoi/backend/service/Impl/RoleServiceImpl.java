package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.IRoleMapper;
import com.tuankhoi.backend.model.entity.Permission;
import com.tuankhoi.backend.model.entity.Role;
import com.tuankhoi.backend.repository.Jpa.IPermissionRepository;
import com.tuankhoi.backend.repository.Jpa.IRoleRepository;
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
    IRoleRepository IRoleRepository;
    IPermissionRepository IPermissionRepository;
    IRoleMapper IRoleMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse create(RoleRequest roleRequest) {
        try {
            Role newRole = IRoleMapper.toRole(roleRequest);
            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = IPermissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            newRole.setPermissions(permissions);
            Role savedRole = IRoleRepository.save(newRole);
            return IRoleMapper.toRoleResponse(savedRole);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create Role due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse findByRoleId(Integer roleId) {
        return IRoleRepository.findById(roleId)
                .map(IRoleMapper::toRoleResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<RoleResponse> findAll() {
        return IRoleRepository.findAll(Sort.by("id"))
                .stream()
                .map(IRoleMapper::toRoleResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public RoleResponse update(Integer roleId, RoleRequest roleRequest) {
        try {
            Role existingRole = IRoleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
            Set<Permission> permissions = new HashSet<>();
            for (int permissionRequestId : roleRequest.getPermissions()) {
                Permission permission = IPermissionRepository.findById(permissionRequestId).orElseThrow(()
                        -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
                permissions.add(permission);
            }
            existingRole.setPermissions(permissions);
            IRoleMapper.updateRole(existingRole, roleRequest);
            return IRoleMapper.toRoleResponse(IRoleRepository.save(existingRole));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update role due to database constraint: " + e.getMessage());
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteByRoleId(Integer roleId) {
        try {
            Role roleToDelete = IRoleRepository.findById(roleId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND));
            IRoleRepository.delete(roleToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete role due to database constraint", e);
        }
    }
}
