package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.PermissionResponse;
import com.tuankhoi.backend.dto.response.RoleResponse;
import com.tuankhoi.backend.dto.response.UserResponse;
import com.tuankhoi.backend.model.Permission;
import com.tuankhoi.backend.model.Role;
import com.tuankhoi.backend.model.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserRequest userUpdateRequest) {
        if ( userUpdateRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userName( userUpdateRequest.getUserName() );
        user.password( userUpdateRequest.getPassword() );
        user.email( userUpdateRequest.getEmail() );
        user.fullName( userUpdateRequest.getFullName() );
        user.active( userUpdateRequest.isActive() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.userName( user.getUserName() );
        userResponse.password( user.getPassword() );
        userResponse.email( user.getEmail() );
        userResponse.fullName( user.getFullName() );
        userResponse.image( user.getImage() );
        userResponse.active( user.isActive() );
        userResponse.createdDate( user.getCreatedDate() );
        userResponse.createdBy( user.getCreatedBy() );
        userResponse.lastModifiedDate( user.getLastModifiedDate() );
        userResponse.lastModifiedBy( user.getLastModifiedBy() );
        userResponse.role( roleToRoleResponse( user.getRole() ) );

        return userResponse.build();
    }

    @Override
    public void updateUser(User user, UserRequest userRequest) {
        if ( userRequest == null ) {
            return;
        }

        user.setUserName( userRequest.getUserName() );
        user.setPassword( userRequest.getPassword() );
        user.setEmail( userRequest.getEmail() );
        user.setFullName( userRequest.getFullName() );
        user.setActive( userRequest.isActive() );
    }

    protected PermissionResponse permissionToPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.id( permission.getId() );
        permissionResponse.name( permission.getName() );
        permissionResponse.description( permission.getDescription() );

        return permissionResponse.build();
    }

    protected Set<PermissionResponse> permissionSetToPermissionResponseSet(Set<Permission> set) {
        if ( set == null ) {
            return null;
        }

        Set<PermissionResponse> set1 = new LinkedHashSet<PermissionResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : set ) {
            set1.add( permissionToPermissionResponse( permission ) );
        }

        return set1;
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.id( role.getId() );
        roleResponse.name( role.getName() );
        roleResponse.description( role.getDescription() );
        roleResponse.permissions( permissionSetToPermissionResponseSet( role.getPermissions() ) );

        return roleResponse.build();
    }
}
