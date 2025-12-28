package com.common.starter.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Utility class for building standardized REST responses.
 * Provides fluent API for creating ResponseEntity with ApiResponse.
 */
public class RestResponseBuilder {

    /**
     * Creates a successful response with data.
     *
     * @param data The response data
     * @param <T>  The type of data
     * @return ResponseEntity with 200 OK status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "Success");
    }

    /**
     * Creates a successful response with data and message.
     *
     * @param data    The response data
     * @param message The success message
     * @param <T>     The type of data
     * @return ResponseEntity with 200 OK status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a created response (201).
     *
     * @param data The created resource
     * @param <T>  The type of data
     * @return ResponseEntity with 201 CREATED status
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return created(data, "Resource created successfully");
    }

    /**
     * Creates a created response with message.
     *
     * @param data    The created resource
     * @param message The success message
     * @param <T>     The type of data
     * @return ResponseEntity with 201 CREATED status
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Creates a no content response (204).
     *
     * @return ResponseEntity with 204 NO CONTENT status
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates an error response.
     *
     * @param message The error message
     * @param status  The HTTP status
     * @param <T>     The type of data
     * @return ResponseEntity with error status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Creates an error response with errors list.
     *
     * @param message The error message
     * @param errors  List of error details
     * @param status  The HTTP status
     * @param <T>     The type of data
     * @return ResponseEntity with error status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, List<String> errors, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Creates a bad request response (400).
     *
     * @param message The error message
     * @param <T>     The type of data
     * @return ResponseEntity with 400 BAD REQUEST status
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a not found response (404).
     *
     * @param message The error message
     * @param <T>     The type of data
     * @return ResponseEntity with 404 NOT FOUND status
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates an unauthorized response (401).
     *
     * @param message The error message
     * @param <T>     The type of data
     * @return ResponseEntity with 401 UNAUTHORIZED status
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a forbidden response (403).
     *
     * @param message The error message
     * @param <T>     The type of data
     * @return ResponseEntity with 403 FORBIDDEN status
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Creates an internal server error response (500).
     *
     * @param message The error message
     * @param <T>     The type of data
     * @return ResponseEntity with 500 INTERNAL SERVER ERROR status
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
