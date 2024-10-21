package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.UpdateProfileRequest;
import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    User toUser(UserRequest userUpdateRequest);

    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "postLikes", ignore = true)
    void updateProfileUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);
}


