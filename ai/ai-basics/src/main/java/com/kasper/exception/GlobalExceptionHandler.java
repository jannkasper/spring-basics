package com.kasper.exception;

import com.kasper.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static ResponseEntity<ApiResponse<?>> errorResponseEntity(String message, HttpStatus status) {
        ApiResponse<?> response = new ApiResponse<>("ERROR", message, null);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpClientErrorException(HttpClientErrorException ex) {
        return errorResponseEntity("Error calling OpenAI API: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse<?>> handleWebClientResponseException(WebClientResponseException ex) {
        return errorResponseEntity("Error with AI service: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<?> handleHttpMessageNotWritableException(HttpMessageNotWritableException ex) {
        logger.error("Message conversion error", ex);
        
        // Check if the error is related to SSE
        if (ex.getMessage().contains("text/event-stream")) {
            // Return a plain text error in SSE format
            String errorMessage = "data: Error: " + ex.getMessage().replace("\n", " ") + "\n\n";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(errorMessage);
        }
        
        // Default handling for other message conversion errors
        return errorResponseEntity("Message conversion error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        logger.error("Null pointer exception", ex);
        
        // Check if this is in an SSE context
        String message = ex.getMessage() != null ? ex.getMessage() : "A null pointer exception occurred";
        if (message.contains("mapper") || message.contains("Flux")) {
            // Likely a Reactive Stream issue
            String errorMessage = "data: Error: Reactive stream processing error\n\n";
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(errorMessage);
        }
        
        return errorResponseEntity("An unexpected error occurred: " + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);
        return errorResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 