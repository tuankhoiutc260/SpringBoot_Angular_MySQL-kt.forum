package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/id/{userId}")
    public APIResponse<UserResponse> getById(@PathVariable String userId) {
        return APIResponse.<UserResponse>builder()
                .result(userService.getById(userId))
                .build();
    }

    @GetMapping("/username/{userName}")
    public APIResponse<UserResponse> getByUserName(@PathVariable String userName) {
        return APIResponse.<UserResponse>builder()
                .result(userService.getByUserName(userName))
                .build();
    }

    @GetMapping("/my-info")
    public APIResponse<UserResponse> getMyInfo() {
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("")
    public APIResponse<List<UserResponse>> getAll() {
        List<UserResponse> userResponseList = userService.getAll();
        return APIResponse.<List<UserResponse>>builder()
                .result(userResponseList)
                .totalRecords(userResponseList.size())
                .build();
    }

    @PostMapping
    public APIResponse<UserResponse> create(@ModelAttribute @Valid UserRequest userRequest) {
        return APIResponse.<UserResponse>builder()
                .result(userService.create(userRequest))
                .build();
    }

    @PutMapping("/{userId}")
    public APIResponse<UserResponse> update(@PathVariable String userId, @ModelAttribute UserRequest userRequest) {
        return APIResponse.<UserResponse>builder()
                .result(userService.update(userId, userRequest))
                .build();
    }

    @DeleteMapping("/{userId}")
    public APIResponse<Void> deleteById(@PathVariable String userId) {
        userService.deleteById(userId);
        return APIResponse.<Void>builder().build();
    }
}
