package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.UserDTO;
import com.tuankhoi.backend.mapper.UserMapper;
import com.tuankhoi.backend.model.Role;
import com.tuankhoi.backend.model.User;
import com.tuankhoi.backend.repository.RoleRepository;
import com.tuankhoi.backend.repository.UserRepository;
import com.tuankhoi.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserDTO> findAll() {
        final List<User> userList = userRepository.findAll(Sort.by("id"));
        return userList.stream()
                .map(UserMapper.INSTANCE::mapToDTO)
                .toList();
    }

    @Override
    public UserDTO findByID(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Role Not Found With ID " + id));
    }

    public UserDTO create(UserDTO userDTO) {
        try {
            Role role = roleRepository.findById(userDTO.getRole_id())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            User user = UserMapper.INSTANCE.mapToEntity(userDTO);
            user.setRole(role);
            User savedUser = userRepository.save(user);
            return UserMapper.INSTANCE.mapToDTO(savedUser);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create user due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }


    public UserDTO update(UUID id, UserDTO userDTO) {
        try {
            // Fetch the existing user from the database
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Fetch the role from the database
            Role role = roleRepository.findById(userDTO.getRole_id())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            // Update the existing user with new values from DTO
            UserMapper.INSTANCE.updateUserFromDTO(userDTO, existingUser);

            // Set the fetched role to the user
            existingUser.setRole(role);

            // Save the updated user entity
            User updatedUser = userRepository.save(existingUser);

            // Map the updated User entity to UserDTO
            return UserMapper.INSTANCE.mapToDTO(updatedUser);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update user due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByID(UUID userID) {
        try {
            User userToDelete = userRepository.findById(userID)
                    .orElseThrow(() -> new EntityNotFoundException("User Not Found With ID " + userID));
            userRepository.delete(userToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete user due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
