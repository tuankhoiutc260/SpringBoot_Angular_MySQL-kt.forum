package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.RoleDTO;
import com.tuankhoi.backend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

//    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
    RoleDTO mapToDTO(Role role);

    Role mapToEntity(RoleDTO roleDTO);

    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
    void updateRoleFromDTO(RoleDTO roleDTO, @MappingTarget Role role);
}
