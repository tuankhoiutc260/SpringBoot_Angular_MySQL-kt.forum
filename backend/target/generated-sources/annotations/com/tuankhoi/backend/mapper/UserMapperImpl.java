package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.UserDTO;
import com.tuankhoi.backend.model.Role;
import com.tuankhoi.backend.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO mapToDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setRole_id( userRoleId( user ) );
        userDTO.setId( user.getId() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setFullName( user.getFullName() );
        userDTO.setActive( user.isActive() );
        userDTO.setCreatedDate( user.getCreatedDate() );
        userDTO.setCreatedBy( user.getCreatedBy() );
        userDTO.setLastModifiedDate( user.getLastModifiedDate() );
        userDTO.setLastModifiedBy( user.getLastModifiedBy() );

        return userDTO;
    }

    @Override
    public User mapToEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setRole( userDTOToRole( userDTO ) );
        user.setId( userDTO.getId() );
        user.setEmail( userDTO.getEmail() );
        user.setPassword( userDTO.getPassword() );
        user.setFullName( userDTO.getFullName() );
        user.setActive( userDTO.isActive() );
        user.setCreatedDate( userDTO.getCreatedDate() );
        user.setCreatedBy( userDTO.getCreatedBy() );
        user.setLastModifiedDate( userDTO.getLastModifiedDate() );
        user.setLastModifiedBy( userDTO.getLastModifiedBy() );

        return user;
    }

    @Override
    public void updateUserFromDTO(UserDTO userDTO, User user) {
        if ( userDTO == null ) {
            return;
        }

        if ( user.getRole() == null ) {
            user.setRole( new Role() );
        }
        userDTOToRole1( userDTO, user.getRole() );
        user.setEmail( userDTO.getEmail() );
        user.setPassword( userDTO.getPassword() );
        user.setFullName( userDTO.getFullName() );
        user.setActive( userDTO.isActive() );
        user.setCreatedDate( userDTO.getCreatedDate() );
        user.setCreatedBy( userDTO.getCreatedBy() );
        user.setLastModifiedDate( userDTO.getLastModifiedDate() );
        user.setLastModifiedBy( userDTO.getLastModifiedBy() );
    }

    private Integer userRoleId(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        Integer id = role.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Role userDTOToRole(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( userDTO.getRole_id() );

        return role;
    }

    protected void userDTOToRole1(UserDTO userDTO, Role mappingTarget) {
        if ( userDTO == null ) {
            return;
        }

        mappingTarget.setId( userDTO.getRole_id() );
    }
}
