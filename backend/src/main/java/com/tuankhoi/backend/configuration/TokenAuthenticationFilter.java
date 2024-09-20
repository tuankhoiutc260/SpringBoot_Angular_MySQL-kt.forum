package com.tuankhoi.backend.configuration;

import com.nimbusds.jwt.SignedJWT;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getTokenFromCookie(request, "access_token");

        if (StringUtils.hasText(accessToken)) {
            try {
                SignedJWT signedJWT = authenticationService.verifyToken(accessToken, false);
                String userId = signedJWT.getJWTClaimsSet().getSubject();
                String scope = signedJWT.getJWTClaimsSet().getStringClaim("scope");

                List<SimpleGrantedAuthority> authorities = Arrays.stream(scope.split(" "))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ParseException e) {
                log.error("Failed to parse JWT token", e);
                throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid JWT format", e);
            } catch (AppException e) {
                log.error("Authentication error: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("Unexpected error during authentication", e);
                SecurityContextHolder.clearContext();
            }
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
}
