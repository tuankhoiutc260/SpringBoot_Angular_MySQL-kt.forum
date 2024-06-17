package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> findAll();

    RoleDTO findByID(final Integer id);

    RoleDTO create(final RoleDTO roleDTO);

    RoleDTO update(final int id, final RoleDTO roleDTO);

    void deleteByID(final Integer id);
}
