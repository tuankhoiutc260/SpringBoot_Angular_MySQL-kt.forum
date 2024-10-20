package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.ChangePasswordRequest;
import com.tuankhoi.backend.dto.request.UpdateProfileRequest;
import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse create(UserRequest userRequest);

    UserResponse getById(String userId);

    UserResponse getByEmail(String username);

    UserResponse getByUserName(String userName);

    UserResponse getMyInfo();

    Page<UserResponse> getAll(int page, int size);

    UserResponse update(String userId, UserRequest userRequest);

    void deleteById(String userId);

    UserResponse updateProfile(String userId, UpdateProfileRequest request);

    void changePassword(String userId, ChangePasswordRequest changePasswordRequest);
}
