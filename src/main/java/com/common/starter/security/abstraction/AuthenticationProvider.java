package com.common.starter.security.abstraction;

import org.springframework.security.core.Authentication;

/**
 * Interface for authentication providers.
 * Allows multiple authentication strategies to be implemented and used
 * interchangeably.
 */
public interface AuthenticationProvider {

    /**
     * Performs authentication with the given credentials.
     *
     * @param credentials The authentication credentials (could be
     *                    username/password, token, etc.)
     * @return An Authentication object if successful
     * @throws AuthenticationException if authentication fails
     */
    Authentication authenticate(Object credentials) throws AuthenticationException;

    /**
     * Indicates whether this provider supports the given credentials type.
     *
     * @param credentialsType The type of credentials
     * @return true if this provider supports the credentials type
     */
    boolean supports(Class<?> credentialsType);

    /**
     * Exception thrown when authentication fails.
     */
    class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
