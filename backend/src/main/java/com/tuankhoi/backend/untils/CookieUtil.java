package com.tuankhoi.backend.untils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${cookie.max-age-remember-me-duration}")
    private int COOKIE_MAX_AGE_REMEMBER_ME_DURATION;

    @Value("${cookie.max-age-default-duration}")
    private int COOKIE_MAX_AGE_DEFAULT_DURATION;

    public void setTokenCookie(HttpServletResponse response, String accessToken, String refreshToken, boolean rememberMe) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(rememberMe ? COOKIE_MAX_AGE_REMEMBER_ME_DURATION : COOKIE_MAX_AGE_DEFAULT_DURATION);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(COOKIE_MAX_AGE_REMEMBER_ME_DURATION);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public void clearTokenCookie(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}