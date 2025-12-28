package com.common.starter.security;

import com.common.starter.security.abstraction.AbstractAuthenticationFilter;
import com.common.starter.security.abstraction.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * JWT-based authentication filter.
 * Extends AbstractAuthenticationFilter to provide JWT-specific authentication
 * logic.
 */
@Component
public class JwtAuthenticationFilter extends AbstractAuthenticationFilter {

    private final TokenProvider<?> tokenProvider;

    public JwtAuthenticationFilter(TokenProvider<?> tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected Authentication authenticate(HttpServletRequest request, String token) {
        if (!tokenProvider.validateToken(token)) {
            return null;
        }

        String username = tokenProvider.extractSubject(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, Collections.emptyList());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }
}
