package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.request.UserRequest;
import com.tuankhoi.backend.dto.response.UserResponse;
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
        userResponse.active( user.isActive() );
        userResponse.createdDate( user.getCreatedDate() );
        userResponse.createdBy( user.getCreatedBy() );
        userResponse.lastModifiedDate( user.getLastModifiedDate() );
        userResponse.lastModifiedBy( user.getLastModifiedBy() );

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
}
