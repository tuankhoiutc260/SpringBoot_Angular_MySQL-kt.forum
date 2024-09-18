package com.tuankhoi.backend.controller;

import com.nimbusds.jose.JOSEException;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.request.LogoutRequest;
import com.tuankhoi.backend.dto.request.RefreshRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication Controller")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @NonFinal
    @Value("${cookie.max-age-remember-me-duration}")
    private int COOKIE_MAX_AGE_REMEMBER_ME_DURATION;

    @NonFinal
    @Value("${cookie.max-age-default-duration}")
    private int COOKIE_MAX_AGE_DEFAULT_DURATION;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected int JWT_VALID_DURATION;

    @Operation(summary = "Login user", description = "Authenticate a user and return a token.")
    @PostMapping("/login")
    public APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse httpServletResponse) {
        AuthenticationResponse authResponse = authenticationService.authenticate(authenticationRequest);

        setTokenCookie(httpServletResponse, authResponse.getToken(), authenticationRequest.isRememberMe());

        return APIResponse.<AuthenticationResponse>builder()
                .result(authResponse)
                .build();
    }

    @Operation(summary = "Introspect token", description = "Check the validity of an authentication token.")
    @PostMapping("/introspect")
    public APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        return APIResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @Operation(summary = "Logout user", description = "Logout the authenticated user.")
    @PostMapping("/logout")
    public APIResponse<Void> logout(@RequestBody LogoutRequest logoutRequest, HttpServletResponse httpServletResponse)
            throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        clearTokenCookie(httpServletResponse);
        return APIResponse.<Void>builder()
                .build();
    }

    @Operation(summary = "Refresh token", description = "Refresh the authentication token.")
    @PostMapping("/refresh-token")
    public APIResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest refreshRequest)
            throws ParseException, JOSEException {
        return APIResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(refreshRequest))
                .build();
    }

    public void setTokenCookie(HttpServletResponse response, String token, boolean rememberMe) {
        Cookie cookie = new Cookie("authToken", token);
        cookie.setHttpOnly(true); // Không thể truy cập từ JavaScript
        cookie.setSecure(true); // Cookie chỉ được gửi qua HTTPS
        cookie.setPath("/"); // Đường dẫn cookie có hiệu lực
        cookie.setMaxAge(rememberMe ? COOKIE_MAX_AGE_REMEMBER_ME_DURATION : COOKIE_MAX_AGE_DEFAULT_DURATION);
        response.addCookie(cookie);
    }

    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
