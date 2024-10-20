package com.tuankhoi.backend.service.Impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tuankhoi.backend.dto.request.AuthenticationRequest;
import com.tuankhoi.backend.dto.request.IntrospectRequest;
import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.dto.response.IntrospectResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.model.entity.InvalidatedToken;
import com.tuankhoi.backend.model.entity.User;
import com.tuankhoi.backend.repository.Jpa.InvalidatedTokenRepository;
import com.tuankhoi.backend.repository.Jpa.UserRepository;
import com.tuankhoi.backend.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    HttpServletRequest request;
    HttpServletResponse response;
    //    https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String JWT_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long JWT_REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.valid-access-token-duration}")
    protected long JWT_VALID_ACCESS_TOKEN_DURATION;

    @NonFinal
    @Value("${jwt.valid-refresh-token-duration}")
    protected long JWT_VALID_REFRESH_TOKEN_DURATION;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_USERNAME_PASSWORD_INVALID));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.USER_USERNAME_PASSWORD_INVALID);
        else if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        addTokenCookie("access_token", accessToken, (int) JWT_VALID_ACCESS_TOKEN_DURATION);
        addTokenCookie("refresh_token", refreshToken, (int) JWT_VALID_REFRESH_TOKEN_DURATION);

        return AuthenticationResponse
                .builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest)
            throws JOSEException, ParseException {
        var token = introspectRequest.getToken();

        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse
                .builder()
                .valid(isValid)
                .build();
    }

    @Override
    public boolean isAuthenticated() throws JOSEException, ParseException {
        var accessToken = getCookieValue("refresh_token");
        if (accessToken == null) {
            return false;
        }
        boolean isValid = true;
        try {
            verifyToken(accessToken, false);
        } catch (AppException e) {
            isValid = false;
        }
        return isValid;
    }

    private void addTokenCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void removeCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public String generateAccessToken(User user) {
        return generateTokenWithExpiry(user, JWT_VALID_ACCESS_TOKEN_DURATION);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateTokenWithExpiry(user, JWT_VALID_REFRESH_TOKEN_DURATION);
    }

    public String generateTokenWithExpiry(User user, long expiryDurationInSeconds) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("tuankhoi.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expiryDurationInSeconds, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(JWT_SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout() throws ParseException, JOSEException {
        String accessToken = getCookieValue("access_token");
        String refreshToken = getCookieValue("refresh_token");

        if (accessToken != null) {
            invalidateToken(accessToken, false);
            removeCookie("access_token");
        }

        if (refreshToken != null) {
            invalidateToken(refreshToken, true);
            removeCookie("refresh_token");
        }
    }

    private void invalidateToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        var signedToken = verifyToken(token, isRefresh);
        String tokenId = signedToken.getJWTClaimsSet().getJWTID();
        Date tokenExpiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(tokenId)
                .expiryTime(tokenExpiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
        log.info("{} token invalidated successfully.", isRefresh ? "Refresh" : "Access");
    }

    private String getCookieValue(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public AuthenticationResponse refreshAccessToken(String refreshTokenRequest) throws ParseException, JOSEException {
        var signedJWT = verifyToken(refreshTokenRequest, true);

        var userID = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findById(userID).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );

        String newAccessToken = generateAccessToken(user);
        addTokenCookie("access_token", newAccessToken, (int) JWT_VALID_ACCESS_TOKEN_DURATION);

        return AuthenticationResponse
                .builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenRequest)
                .authenticated(true)
                .build();
    }

    @Override
    public SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(JWT_SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(JWT_REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    @Override
    public String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRole() != null) {
            stringJoiner.add("ROLE_" + user.getRole().getName());
            if (!CollectionUtils.isEmpty(user.getRole().getPermissions())) {
                user.getRole().getPermissions().forEach(permission ->
                        stringJoiner.add(permission.getName()));
            }
        }
        return stringJoiner.toString();
    }

    @Override
    public Authentication getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication;
        } else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
}
