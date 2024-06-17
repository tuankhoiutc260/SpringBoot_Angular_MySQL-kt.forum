package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.RoleDTO;
import com.tuankhoi.backend.model.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDTO mapToDTO(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId( role.getId() );
        roleDTO.setName( role.getName() );
        roleDTO.setDescription( role.getDescription() );

        return roleDTO;
    }

    @Override
    public Role mapToEntity(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( roleDTO.getId() );
        role.setName( roleDTO.getName() );
        role.setDescription( roleDTO.getDescription() );

        return role;
    }

    @Override
    public void updateRoleFromDTO(RoleDTO roleDTO, Role role) {
        if ( roleDTO == null ) {
            return;
        }

        role.setName( roleDTO.getName() );
        role.setDescription( roleDTO.getDescription() );
    }
}
