package com.tuankhoi.backend.service;

import com.nimbusds.jose.JOSEException;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import org.springframework.security.core.Authentication;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    IntrospectResponse introspectResponse(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    Authentication getAuthenticatedUser();

}
