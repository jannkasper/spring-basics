package com.kasper.exception;

import com.kasper.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to create error response
    private static ResponseEntity<ApiResponse<?>> errorResponseEntity(String message, HttpStatus status) {
        ApiResponse<?> response = ApiResponse.error(message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorResponseEntity("Bad Request: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Catch-all for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        // Log the exception details for debugging
        // logger.error("An unexpected error occurred", ex);
        return errorResponseEntity("An internal server error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 