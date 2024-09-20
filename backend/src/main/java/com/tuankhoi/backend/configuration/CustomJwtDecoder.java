package com.tuankhoi.backend.configuration;

import com.nimbusds.jose.JOSEException;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    String JWT_SIGNER_KEY;

    NimbusJwtDecoder nimbusJwtDecoder;

    final AuthenticationService authenticationService;

    public CustomJwtDecoder(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private synchronized void initializeDecoder() {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(JWT_SIGNER_KEY.getBytes(), "HmacSHA512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());
            if (!response.isValid()) {
                throw new JwtException("Token invalid");
            }
        } catch (ParseException | JOSEException e) {
            throw new JwtException("Error when introspect token: " + e.getMessage(), e);
        }

        initializeDecoder();

        return nimbusJwtDecoder.decode(token);
    }
}
