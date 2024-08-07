package com.tuankhoi.backend.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.request.LogoutRequest;
import com.tuankhoi.backend.dto.request.RefreshRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.entity.User;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    Authentication getAuthenticatedUser();

    String generateToken(User user);

    void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException;

    AuthenticationResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException;

    SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException;

    String buildScope(User user);
}
