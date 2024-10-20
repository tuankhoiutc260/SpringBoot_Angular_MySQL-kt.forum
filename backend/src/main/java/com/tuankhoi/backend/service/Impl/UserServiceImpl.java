package com.tuankhoi.backend.service.Impl;

import com.tuankhoi.backend.dto.request.ChangePasswordRequest;
import com.tuankhoi.backend.dto.request.UpdateProfileRequest;
import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.CloudinaryResponse;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.enums.RoleEnum;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.mapper.UserMapper;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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
            newUser.setFullName(userRequest.getUserName());
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

        try {
            existingUser.setUserName(Optional.ofNullable(userRequest.getUserName())
                    .filter(name -> !name.isEmpty())
                    .orElse(existingUser.getUserName()));

            existingUser.setPassword(Optional.ofNullable(userRequest.getPassword())
                    .filter(password -> !password.isEmpty())
                    .map(passwordEncoder::encode)
                    .orElse(existingUser.getPassword()));

            existingUser.setEmail(Optional.ofNullable(userRequest.getEmail())
                    .filter(email -> !email.isEmpty())
                    .orElse(existingUser.getEmail()));

            existingUser.setFullName(Optional.ofNullable(userRequest.getFullName())
                    .filter(fullName -> !fullName.isEmpty())
                    .orElse(existingUser.getFullName()));

            Optional.ofNullable(userRequest.getImageFile())
                    .filter(imageFile -> !imageFile.isEmpty())
                    .ifPresent(imageFile -> handleImageUpdate(existingUser, imageFile));

            existingUser.setRole(Optional.ofNullable(userRequest.getRoleId())
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND)))
                    .orElseGet(() -> roleRepository.findById(existingUser.getRole().getId())
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOTFOUND))));

            User updatedUser = userRepository.save(existingUser);
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

        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(imageFile, imageFileName);
        user.setImageUrl(cloudinaryResponse.getUrl());
        user.setCloudinaryImageId(cloudinaryResponse.getPublicId());
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = {"userByEmail", "userByUserName", "allUsers"}, allEntries = true)
    })
    @Override
    @Transactional
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

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = {"userByEmail", "userByUserName", "allUsers"}, allEntries = true)
    })
    @Override
    @Transactional
    public UserResponse updateProfile(String userId, UpdateProfileRequest updateProfileRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        try {
            userMapper.updateProfileUserFromRequest(updateProfileRequest, existingUser);

            Optional.ofNullable(updateProfileRequest.getImageFile())
                    .filter(imageFile -> !imageFile.isEmpty())
                    .ifPresent(imageFile -> handleImageUpdate(existingUser, imageFile));

            User updatedUser = userRepository.save(existingUser);
            return userMapper.toUserResponse(updatedUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Data Integrity Violation:", e);
            throw new AppException(ErrorCode.DATABASE_CONSTRAINT_VIOLATION, "Failed to Update User due to database constraint: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unknown Error while updating user:", e);
            throw new AppException(ErrorCode.UNKNOWN_ERROR, "Failed to Update User", e);
        }
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest changePasswordRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_USERNAME_PASSWORD_INVALID));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), existingUser.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.USER_USERNAME_PASSWORD_INVALID);
        else if (!existingUser.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }

        existingUser.setPassword(passwordEncoder.encode(passwordEncoder.encode(changePasswordRequest.getNewPassword())));
        userRepository.save(existingUser);
    }
}
