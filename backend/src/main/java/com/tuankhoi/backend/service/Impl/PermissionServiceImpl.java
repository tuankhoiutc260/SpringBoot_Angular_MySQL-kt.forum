package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.PermissionMapper;
import com.tuankhoi.backend.model.entity.Permission;
import com.tuankhoi.backend.repository.Jpa.PermissionRepository;
import com.tuankhoi.backend.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse create(PermissionRequest permissionRequest) {
        try {
            if (permissionRepository.findByName(permissionRequest.getName()).isPresent())
                throw new AppException(ErrorCode.PERMISSION_EXISTED);
            Permission newPermission = permissionMapper.toPermission(permissionRequest);

            return permissionMapper.toPermissionResponse(permissionRepository.save(newPermission));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Create Permission due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Create Permission", e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse getByName(String permissionName) {
        return permissionRepository
                .findByName(permissionName)
                .map(permissionMapper::toPermissionResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse update(Integer permissionRequestId, PermissionRequest permissionRequest) {
        try {
            Permission existingPermission = permissionRepository.findById(permissionRequestId).orElseThrow(() ->
                    new AppException(ErrorCode.PERMISSION_NOTFOUND));
            permissionMapper.updatePermissionFromRequest(permissionRequest, existingPermission);

            return permissionMapper.toPermissionResponse(permissionRepository.save(existingPermission));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Update Permission due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Update Permission", e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(Integer permissionId) {
        try {
            Permission permissionToDelete = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOTFOUND));

            permissionRepository.delete(permissionToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to Delete Permission due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Delete Permission", e);
        }
    }
}
