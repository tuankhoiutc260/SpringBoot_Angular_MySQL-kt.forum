package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse create(UserRequest userRequest);

    UserResponse getById(String userId);

    UserResponse getByEmail(String username);

    UserResponse getByUserName(String userName);

    UserResponse getMyInfo();

    List<UserResponse> getAll();

    UserResponse update(String userId, UserRequest userRequest);

    void deleteById(String userId);
}
