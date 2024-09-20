package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.enums.RoleEnum;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.UserMapper;
import com.tuankhoi.backend.model.entity.Role;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.RoleRepository;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import com.tuankhoi.backend.service.CloudinaryService;
import com.tuankhoi.backend.service.UserService;
import com.tuankhoi.backend.untils.FileUploadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RoleRepository roleRepository;
    CloudinaryService cloudinaryService;

    @NonFinal
    @Value("${image.url.default}")
    private String imageUrlDefault;

    @NonFinal
    @Value("${image.id.default}")
    private String imageIdDefault;

    @CacheEvict(value = {"user", "userByEmail", "userByUserName", "allUsers"}, allEntries = true)
    @Override
    public UserResponse create(UserRequest userRequest) {
        if (userRepository.findByUserName(userRequest.getUserName()).isPresent())
            throw new AppException(ErrorCode.USER_EXISTED);
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent())
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);

        try {
            User newUser = userMapper.toUser(userRequest);
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            newUser.setImageUrl(imageUrlDefault);
            newUser.setCloudinaryImageId(imageIdDefault);

            if (userRequest.getRoleId() == null)
                newUser.setRole(roleRepository.findByName(RoleEnum.USER.name()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            else
                newUser.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));

            return userMapper.toUserResponse(userRepository.save(newUser));
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Create User due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Create User", e);
        }
    }

    @Cacheable(value = "user", key = "#userId", unless = "#result == null")
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
        return userRepository.findByUserName(userName)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @Cacheable(value = "userByEmail", key = "#email", unless = "#result == null")
    @Override
    public UserResponse getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @Cacheable(value = "userByUserName", key = "#userName", unless = "#result == null")
    @Override
    public UserResponse getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @Cacheable(value = "allUsers", key = "'page:' + #page + ',size:' + #size", unless = "#result.isEmpty()")
    @Override
    public Page<UserResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserResponse);
    }

//    @PostAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = {"userByEmail", "userByUserName", "allUsers"}, allEntries = true)
    })
    @Override
    @Transactional
    public UserResponse update(String userId, UserRequest userRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        log.info("Existing user: {}", existingUser);
        try {
            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            }

            if (userRequest.getRoleId() == null)
                existingUser.setRole(roleRepository.findById(existingUser.getRole().getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));
            else
                existingUser.setRole(roleRepository.findById(userRequest.getRoleId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)));

            if (userRequest.getImageFile() != null && !userRequest.getImageFile().isEmpty()) {
                handleImageUpdate(existingUser, userRequest.getImageFile());
            }

            userMapper.updateUserFromRequest(userRequest, existingUser);
            User updatedUser = userRepository.save(existingUser);
            log.info(updatedUser.toString());
            return userMapper.toUserResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Data Integrity Violation:", e);
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Update User due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unknown Error while updating user:", e);
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update User", e);
        }
    }

    private void handleImageUpdate(User user, MultipartFile imageFile) {
        FileUploadUtil.assertAllowed(imageFile, FileUploadUtil.IMAGE_PATTERN);
        String imageFileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(imageFile.getOriginalFilename()));

//        if (user.getCloudinaryImageId() != null) {
//            cloudinaryService.deleteImage(user.getCloudinaryImageId());
//        }

        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);
        user.setImageUrl(cloudinaryResponse.getUrl());
        user.setCloudinaryImageId(cloudinaryResponse.getPublicId());
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = {"userByEmail", "userByUserName", "allUsers"}, allEntries = true)
    })
    @Override
    public void deleteById(String userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        try {
            if (userToDelete.getCloudinaryImageId() != null) {
                cloudinaryService.deleteImage(userToDelete.getCloudinaryImageId());
            }

            userRepository.delete(userToDelete);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Delete User due to database constraint", e);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Delete User", e);
        }
    }
}
