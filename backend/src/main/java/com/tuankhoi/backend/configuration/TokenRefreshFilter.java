package com.tuankhoi.backend.configuration;

import com.tuankhoi.backend.dto.response.AuthenticationResponse;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenRefreshFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getTokenFromCookie(request, "access_token");
        String refreshToken = getTokenFromCookie(request, "refresh_token");

        try {
            if (!StringUtils.hasText(accessToken) && StringUtils.hasText(refreshToken)) {
                AuthenticationResponse authResponse = authenticationService.refreshAccessToken(refreshToken);
                if (authResponse != null && authResponse.isAuthenticated()) {
                    request.setAttribute("access_token", authResponse.getAccessToken());
                    setAuthentication(authResponse.getAccessToken());
                } else {
                    log.warn("Token refresh failed or token is invalid");
                }
            } else if (StringUtils.hasText(accessToken)) {
                authenticationService.verifyToken(accessToken, false);
                setAuthentication(accessToken);
            }
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.INVALID_TOKEN) {
                log.error("Invalid access token: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            } else {
                log.error("System error: {}", e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "System error");
            }
        } catch (Exception e) {
            log.error("Unknown error: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown error");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setAuthentication(String token) {
        try {
            String userId = authenticationService.verifyToken(token, false).getJWTClaimsSet().getSubject();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.INVALID_TOKEN) {
                log.error("Invalid token: {}", e.getMessage(), e);
            } else {
                log.error("Error during token verification: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Failed to set user authentication in the security context", e);
        }
    }
}
