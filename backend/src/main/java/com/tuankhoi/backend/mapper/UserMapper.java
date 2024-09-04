package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "role.id", source = "roleId")
    User toUser(UserRequest userUpdateRequest);

    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "cloudinaryImageId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "role.id", source = "roleId")
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);
}


