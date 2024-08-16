package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse create(UserRequest userRequest);

    UserResponse findByUserId(String userId);

    UserResponse findByEmail(String username);

    UserResponse findByUserName(String userName);

    UserResponse getMyInfo();

    List<UserResponse> findAll();

    UserResponse update(String userId, UserRequest userRequest);

    void deleteByUserId(String userId);
}
