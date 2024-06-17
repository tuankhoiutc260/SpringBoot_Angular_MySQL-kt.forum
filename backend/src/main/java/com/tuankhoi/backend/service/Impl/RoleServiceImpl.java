package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.RoleDTO;
import com.tuankhoi.backend.mapper.RoleMapper;
import com.tuankhoi.backend.model.Role;
import com.tuankhoi.backend.repository.RoleRepository;
import com.tuankhoi.backend.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDTO> findAll() {
        final List<Role> roleList = roleRepository.findAll(Sort.by("id"));
        return roleList.stream()
                .map(RoleMapper.INSTANCE::mapToDTO)
                .toList();
    }

    @Override
    public RoleDTO findByID(Integer id) {
        return roleRepository.findById(id)
                .map(RoleMapper.INSTANCE::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Role Not Found With ID " + id));
    }

    @Override
    public RoleDTO create(RoleDTO roleDTO) {
        try {
            Role newRole = RoleMapper.INSTANCE.mapToEntity(roleDTO);
            return RoleMapper.INSTANCE.mapToDTO(roleRepository.save(newRole));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create role due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create role: " + e.getMessage());
        }
    }

    @Override
    public RoleDTO update(int id, RoleDTO roleDTO) {
        try {
            Role existingRole = roleRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Role Not Found With ID " + id));
            RoleMapper.INSTANCE.updateRoleFromDTO(roleDTO, existingRole);
            return RoleMapper.INSTANCE.mapToDTO(roleRepository.save(existingRole));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update role due to database constraint: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update role: " + e.getMessage());
        }
    }

    @Override
    public void deleteByID(Integer roleID) {
        try {
            Role roleToDelete = roleRepository.findById(roleID)
                    .orElseThrow(() -> new EntityNotFoundException("Role Not Found With ID " + roleID));
            roleRepository.delete(roleToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete role due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete role", e);
        }
    }
}
