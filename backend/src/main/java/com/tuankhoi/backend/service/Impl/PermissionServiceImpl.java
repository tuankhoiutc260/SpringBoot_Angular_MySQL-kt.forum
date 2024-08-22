package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.PermissionRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.IPermissionMapper;
import com.tuankhoi.backend.model.entity.Permission;
import com.tuankhoi.backend.repository.Jpa.IPermissionRepository;
import com.tuankhoi.backend.service.IPermissionService;
import jakarta.persistence.EntityNotFoundException;
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
public class PermissionServiceImpl implements IPermissionService {
    IPermissionRepository IPermissionRepository;
    IPermissionMapper IPermissionMapper;

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse create(PermissionRequest permissionRequest) {
        try {
            if (IPermissionRepository.findByName(permissionRequest.getName()).isPresent())
                throw new AppException(ErrorCode.PERMISSION_EXISTED);
            Permission newPermission = IPermissionMapper.toPermission(permissionRequest);
            return IPermissionMapper.toPermissionResponse(IPermissionRepository.save(newPermission));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create permission due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse findByName(String permissionName) {
        return IPermissionRepository
                .findByName(permissionName)
                .map(IPermissionMapper::toPermissionResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public List<PermissionResponse> findAll() {
        var permissionList = IPermissionRepository.findAll();
        return permissionList
                .stream()
                .map(IPermissionMapper::toPermissionResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public PermissionResponse update(Integer permissionRequestId, PermissionRequest permissionRequest) {
        try {
            Permission existingPermission = IPermissionRepository.findById(permissionRequestId).orElseThrow(()->
                    new AppException(ErrorCode.PERMISSION_NOTFOUND));
            IPermissionMapper.updatePermission(existingPermission, permissionRequest);
            return IPermissionMapper.toPermissionResponse(IPermissionRepository.save(existingPermission));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update permission due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteByPermissionId(Integer permissionId) {
        try {
            Permission permissionToDelete = IPermissionRepository.findById(permissionId)
                    .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOTFOUND));
            IPermissionRepository.delete(permissionToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete permission due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete permission", e);
        }
    }
}
