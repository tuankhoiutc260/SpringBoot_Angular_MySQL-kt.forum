package com.tuankhoi.backend.controller;

import com.nimbusds.jose.JOSEException;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.response.APIResponse;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication Controller")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @Operation(summary = "Login user", description = "Authenticate a user and return a token.")
    @PostMapping("/login")
    public APIResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authResponse = authenticationService.login(authenticationRequest);
        return APIResponse.<AuthenticationResponse>builder()
                .result(authResponse)
                .build();
    }

    @Operation(summary = "Introspect token", description = "Check the validity of an authentication token.")
    @PostMapping("/introspect")
    public APIResponse<IntrospectResponse> introspectToken(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        return APIResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @Operation(summary = "Check if authenticated", description = "Check if the current user is authenticated.")
    @GetMapping("/is-authenticated")
    public APIResponse<Boolean> authenticate()
            throws ParseException, JOSEException {
        return APIResponse.<Boolean>builder()
                .result(authenticationService.isAuthenticated())
                .build();
    }

    @Operation(summary = "Logout user", description = "Logout the authenticated user.")
    @PostMapping("/logout")
    public APIResponse<Void> logout()
            throws ParseException, JOSEException {
        authenticationService.logout();
        return APIResponse.<Void>builder().build();
    }

    @Operation(summary = "Refresh token", description = "Refresh the authentication token.")
    @PostMapping("/refresh-access-token")
    public APIResponse<AuthenticationResponse> refreshAccessToken(@CookieValue(name = "refreshToken") String refreshTokenRequest)
            throws ParseException, JOSEException {
        return APIResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshAccessToken(refreshTokenRequest))
                .build();
    }
}
