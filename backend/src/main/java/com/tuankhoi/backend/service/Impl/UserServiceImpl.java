package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.enums.RoleEnum;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.UserMapper;
import com.tuankhoi.backend.model.User;
import com.tuankhoi.backend.repository.RoleRepository;
import com.tuankhoi.backend.repository.UserRepository;
import com.tuankhoi.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;


//    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse create(UserRequest userRequest) {
        try {
            if (userRepository.findByUserName(userRequest.getUserName()).isPresent())
                throw new AppException(ErrorCode.USER_EXISTED);
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent())
                throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
            User newUser = userMapper.toUser(userRequest);
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            if (userRequest.getRole() == null)
                newUser.setRole(roleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            else
                newUser.setRole(roleRepository.findById(userRequest.getRole()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            return userMapper.toUserResponse(userRepository.save(newUser));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create user due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByID(String userID) {
        return userRepository.findById(userID)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PostAuthorize("returnObject.userName == authentication.name")
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        return findByUserName(userName);
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByEmail(String username) {
        return userRepository.findByEmail(username)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByUserName(String userName) {
        return userRepository
                .findByUserName(userName)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PostAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CREATE_POST')")
    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll(Sort.by("id"))
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse update(String userID, UserRequest userRequest) {
        try {
            User existingUser = userRepository.findById(userID)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            existingUser.setRole(roleRepository.findById(userRequest.getRole()).orElseThrow(()
                    -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            userMapper.updateUser(existingUser, userRequest);
            User user = userRepository.save(existingUser);
            return userMapper.toUserResponse(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update user due to database constraint: " + e.getMessage(), e);
        }
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteByID(String userID) {
        try {
            User userToDelete = userRepository.findById(userID)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
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
