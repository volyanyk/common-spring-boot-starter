package com.common.starter.service.exception;

/**
 * Base exception for service layer.
 * All service-specific exceptions should extend this class.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
