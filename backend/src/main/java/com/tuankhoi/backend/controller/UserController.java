package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Controller")
public class UserController {
    UserService userService;

    @Operation(summary = "Create new user", description = "Create a new user.")
    @PostMapping
    public APIResponse<UserResponse> create(@ModelAttribute UserRequest userRequest) {
        return APIResponse.<UserResponse>builder()
                .result(userService.create(userRequest))
                .build();
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their user ID.")
    @GetMapping("/id/{userId}")
    public APIResponse<UserResponse> getById(@PathVariable String userId) {
        return APIResponse.<UserResponse>builder()
                .result(userService.getById(userId))
                .build();
    }

    @Operation(summary = "Get user by username", description = "Retrieve a user by their username.")
    @GetMapping("/username/{userName}")
    public APIResponse<UserResponse> getByUserName(@PathVariable String userName) {
        return APIResponse.<UserResponse>builder()
                .result(userService.getByUserName(userName))
                .build();
    }

    @Operation(summary = "Get my information", description = "Retrieve the authenticated user's information.")
    @GetMapping("/my-info")
    public APIResponse<UserResponse> getMyInfo() {
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @GetMapping("")
    public APIResponse<Page<UserResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> userResponsePage = userService.getAll(page, size);
        return APIResponse.<Page<UserResponse>>builder()
                .result(userResponsePage)
                .build();
    }

    @Operation(summary = "Update user", description = "Update a user's details by user ID.")
    @PutMapping("/{userId}")
    public APIResponse<UserResponse> update(@PathVariable String userId,
                                            @ModelAttribute UserRequest userRequest) {
        log.info(userRequest.toString());
        return APIResponse.<UserResponse>builder()
                .result(userService.update(userId, userRequest))
                .build();
    }

    @Operation(summary = "Delete user", description = "Delete a user by user ID.")
    @DeleteMapping("/{userId}")
    public APIResponse<Void> deleteById(@PathVariable String userId) {
        userService.deleteById(userId);
        return APIResponse.<Void>builder().build();
    }
}
