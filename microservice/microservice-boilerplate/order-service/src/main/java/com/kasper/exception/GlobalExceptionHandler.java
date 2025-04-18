package com.kasper.exception;

import com.kasper.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import feign.FeignException;

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

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse<?>> handleRestClientException(RestClientException ex) {
        // Handles errors during RestTemplate calls
        return errorResponseEntity("Error calling service via RestTemplate: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        // Handles errors during Feign client calls
        HttpStatus status = HttpStatus.resolve(ex.status()) != null ? HttpStatus.resolve(ex.status()) : HttpStatus.SERVICE_UNAVAILABLE;
        String message = String.format("Error calling service via Feign [%s]: %s", status, ex.getMessage());
        // Consider logging ex.contentUTF8() for more details if needed
        return errorResponseEntity(message, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        // Often used for service discovery issues
        return errorResponseEntity("Service unavailable or configuration error: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Catch-all for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        // Log the exception details for debugging
        // logger.error("An unexpected error occurred", ex);
        return errorResponseEntity("An internal server error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 