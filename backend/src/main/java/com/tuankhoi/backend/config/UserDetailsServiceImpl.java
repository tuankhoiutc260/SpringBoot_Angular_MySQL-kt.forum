package com.tuankhoi.backend.config;

import com.tuankhoi.backend.dto.UserDTO;
import com.tuankhoi.backend.service.RoleService;
import com.tuankhoi.backend.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final RoleService roleService;

    public UserDetailsServiceImpl(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = userService.findByEmail(username);
        return User
                .withUsername(username)
                .password(userDTO.getPassword())
                .roles(roleService.findByID(userDTO.getRole_id()).getName().name())
                .disabled(!userDTO.isActive())
                .build();
    }
}
