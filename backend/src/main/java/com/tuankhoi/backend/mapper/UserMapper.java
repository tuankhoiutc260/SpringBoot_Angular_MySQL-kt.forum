package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.UserDTO;
import com.tuankhoi.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

//@Mapper
//public interface UserMapper {
//    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
//
//    //    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
//    @Mapping(source = "role.id", target = "role_id")
//    UserDTO mapToDTO(User user);
//
//    @Mapping(source = "role_id", target = "role.id")
//    User mapToEntity(UserDTO userDTO);
//
//    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
//    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);
//}

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "role.id", target = "role_id")
    UserDTO mapToDTO(User user);

    @Mapping(source = "role_id", target = "role.id")
    User mapToEntity(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "role_id", target = "role.id")
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);
}


