package com.tuankhoi.backend.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.model.entity.User;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    Authentication getAuthenticatedUser();

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    void logout() throws ParseException, JOSEException;

    AuthenticationResponse refreshAccessToken(String refreshTokenRequest) throws ParseException, JOSEException ;

    SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException;

    String buildScope(User user);
}
