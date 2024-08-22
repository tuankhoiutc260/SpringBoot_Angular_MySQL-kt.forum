package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.enums.RoleEnum;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.IUserMapper;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.IRoleRepository;
import com.tuankhoi.backend.repository.Jpa.IUserRepository;
import com.tuankhoi.backend.service.IUserService;
import com.tuankhoi.backend.untils.ImageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements IUserService {
    IUserRepository IUserRepository;
    PasswordEncoder passwordEncoder;
    IUserMapper IUserMapper;
    IRoleRepository IRoleRepository;

    @NonFinal
    @Value("${default.avatar.image.path}")
    private String defaultAvatarImagePath;

    @Override
    public UserResponse create(UserRequest userRequest) {
        try {
            if (IUserRepository.findByUserName(userRequest.getUserName()).isPresent())
                throw new AppException(ErrorCode.USER_EXISTED);
            if (IUserRepository.findByEmail(userRequest.getEmail()).isPresent())
                throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
            User newUser = IUserMapper.toUser(userRequest);
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            String base64Image;
            try {
                base64Image = ImageUtil.getImageAsBase64(defaultAvatarImagePath);
            } catch (NoSuchFileException e) {
                throw new RuntimeException("Default avatar image file not found: " + e.getMessage(), e);
            }

            newUser.setImage(base64Image);
            if (userRequest.getRole() == null)
                newUser.setRole(IRoleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            else
                newUser.setRole(IRoleRepository.findById(userRequest.getRole()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            return IUserMapper.toUserResponse(IUserRepository.save(newUser));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to create user due to database constraint: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByUserId(String userId) {
        return IUserRepository.findById(userId)
                .map(IUserMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PostAuthorize("returnObject.userName == authentication.name")
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        return findByUserName(userName);
    }

//    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByEmail(String username) {
        return IUserRepository.findByEmail(username)
                .map(IUserMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

//    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse findByUserName(String userName) {
        return IUserRepository
                .findByUserName(userName)
                .map(IUserMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

//    @PostAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('CREATE_POST')")
    @Override
    public List<UserResponse> findAll() {
        return IUserRepository.findAll(Sort.by("id"))
                .stream()
                .map(IUserMapper::toUserResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse update(String userId, UserRequest userRequest) {
        try {
            User existingUser = IUserRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            existingUser.setRole(IRoleRepository.findById(userRequest.getRole()).orElseThrow(()
                    -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            IUserMapper.updateUser(existingUser, userRequest);
            User user = IUserRepository.save(existingUser);
            return IUserMapper.toUserResponse(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to update user due to database constraint: " + e.getMessage(), e);
        }
    }

//    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteByUserId(String userId) {
        try {
            User userToDelete = IUserRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            IUserRepository.delete(userToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new IllegalArgumentException("Failed to delete user due to database constraint", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
