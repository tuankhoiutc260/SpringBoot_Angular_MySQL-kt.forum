package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    User toUser(UserRequest userUpdateRequest);

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest userRequest);
}


