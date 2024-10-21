package com.tuankhoi.backend.configuration;

import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration implements WebMvcConfigurer {
    @Autowired
    private UserRepository userRepository;

    String[] PUBLIC_ENDPOINTS = {
            // WebSocket
            "/ws/**",
            "/ws",
            // Swagger
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",

            "/api/v1/auth/**",

            "/api/v1/category/**",

            "/api/v1/posts/**",
            "/api/v1/likes/**",
            "/api/v1/users/**",
            "/api/comments/post/**",
            "/api/comments",
    };

    String[] PUBLIC_ENDPOINTS_FOR_GET = {
            "/api/v1/categories",
            "/api/v1/categories/**",

            "/api/v1/sub-categories",
            "/api/v1/sub-categories/**",

            "/api/v1/post",
            "/api/v1/post/**",

            "/api/v1/comments",
            "/api/v1/comments/**",

            "/api/v1/likes/count-likes",

            "/api/v1/users/id/**",
            "/api/v1/users/username/**"
    };

    String[] PUBLIC_ENDPOINTS_FOR_POST = {
            "/api/v1/users"
    };


    TokenAuthenticationFilter tokenAuthenticationFilter;
    TokenRefreshFilter tokenRefreshFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_FOR_GET).permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_FOR_POST).permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(tokenRefreshFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuditorAware<User> auditingAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty();
            }
            String userId = authentication.getName();
            return userRepository.findById(userId);
        };
    }
}
