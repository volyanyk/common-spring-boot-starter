package com.common.starter.security.abstraction;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Abstract base class for authentication filters.
 * Implements the template method pattern for authentication flow.
 * Subclasses should implement specific authentication logic.
 */
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Template method for filter processing.
     * Handles the common authentication flow.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && shouldAuthenticate(request, token)) {
                Authentication authentication = authenticate(request, token);

                if (authentication != null && authentication.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    onSuccessfulAuthentication(request, response, authentication);
                } else {
                    onFailedAuthentication(request, response, token);
                }
            }
        } catch (Exception ex) {
            onAuthenticationException(request, response, ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the authentication token from the request.
     * Default implementation extracts Bearer token from Authorization header.
     *
     * @param request The HTTP request
     * @return The extracted token, or null if not found
     */
    protected String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Determines if authentication should be attempted for this request.
     * Can be overridden to implement custom logic (e.g., skip certain paths).
     *
     * @param request The HTTP request
     * @param token   The extracted token
     * @return true if authentication should be attempted, false otherwise
     */
    protected boolean shouldAuthenticate(HttpServletRequest request, String token) {
        return true;
    }

    /**
     * Performs the actual authentication using the extracted token.
     * Must be implemented by subclasses.
     *
     * @param request The HTTP request
     * @param token   The authentication token
     * @return The Authentication object if successful, null otherwise
     */
    protected abstract Authentication authenticate(HttpServletRequest request, String token);

    /**
     * Hook method called after successful authentication.
     * Can be overridden to perform additional actions.
     *
     * @param request        The HTTP request
     * @param response       The HTTP response
     * @param authentication The successful authentication
     */
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        // Default: do nothing
    }

    /**
     * Hook method called after failed authentication.
     * Can be overridden to perform additional actions.
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param token    The token that failed authentication
     */
    protected void onFailedAuthentication(HttpServletRequest request, HttpServletResponse response, String token) {
        // Default: do nothing
    }

    /**
     * Hook method called when an exception occurs during authentication.
     * Can be overridden to handle exceptions differently.
     *
     * @param request   The HTTP request
     * @param response  The HTTP response
     * @param exception The exception that occurred
     */
    protected void onAuthenticationException(HttpServletRequest request, HttpServletResponse response,
            Exception exception) {
        logger.error("Authentication error", exception);
    }
}
