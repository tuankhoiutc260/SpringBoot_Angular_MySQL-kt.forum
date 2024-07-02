package com.tuankhoi.backend.service;

import com.nimbusds.jose.JOSEException;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.model.User;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    IntrospectResponse introspectResponse(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    Authentication getAuthenticatedUser();

    String generateToken(User user);
    String buildScope(User user);

}
