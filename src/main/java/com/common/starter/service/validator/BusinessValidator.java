package com.common.starter.service.validator;

import com.common.starter.service.exception.ValidationException;

/**
 * Interface for business rule validation.
 * Implementations can be chained for complex validation scenarios.
 *
 * @param <T> The type to validate
 */
public interface BusinessValidator<T> {

    /**
     * Validates the given object.
     *
     * @param object The object to validate
     * @throws ValidationException if validation fails
     */
    void validate(T object) throws ValidationException;

    /**
     * Indicates whether this validator supports the given type.
     *
     * @param type The type to check
     * @return true if this validator supports the type
     */
    boolean supports(Class<?> type);
}
