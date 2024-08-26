package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.enums.RoleEnum;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.UserMapper;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.RoleRepository;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import com.tuankhoi.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${default.avatar.image.path}")
    private String defaultUserImageUrl;

    @NonFinal
    @Value("${default.avatar.image.path}")
    private String defaultCloudinaryUserImageId;

    @Override
    public UserResponse create(UserRequest userRequest) {
        if (userRepository.findByUserName(userRequest.getUserName()).isPresent())
            throw new AppException(ErrorCode.USER_EXISTED);
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent())
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);

        try {
            User newUser = userMapper.toUser(userRequest);
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            newUser.setImageUrl(defaultUserImageUrl);
            newUser.setCloudinaryImageId(defaultCloudinaryUserImageId);

            if (userRequest.getRoleId() == null)
                newUser.setRole(roleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            else
                newUser.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));

            return userMapper.toUserResponse(userRepository.save(newUser));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create User due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR,"Failed to Create User", e);
        }
    }

    @Override
    public UserResponse getById(String userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PostAuthorize("returnObject.userName == authentication.name")
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();
        return getByUserName(userName);
    }

    @Override
    public UserResponse getByEmail(String username) {
        return userRepository.findByEmail(username)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @Override
    public UserResponse getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @Override
    public Page<UserResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapper::toUserResponse);
    }

    @PostAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse update(String userId, UserRequest userRequest) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            existingUser.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow(()
                    -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            userMapper.updateUserFromRequest(userRequest, existingUser);
            User user = userRepository.save(existingUser);
            return userMapper.toUserResponse(user);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Update User due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR,"Failed to Update User", e);
        }
    }

    @Override
    public void deleteById(String userId) {
        try {
            User userToDelete = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
            userRepository.delete(userToDelete);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete User due to database constraint", e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR,"Failed to Delete User", e);
        }
    }
}
