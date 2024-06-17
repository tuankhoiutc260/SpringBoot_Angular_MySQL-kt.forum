package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll();

    UserDTO findByID(final UUID id);

    UserDTO create(final UserDTO userDTO);

    UserDTO update(final UUID id, final UserDTO userDTO);

    void deleteByID(final UUID id);
}
