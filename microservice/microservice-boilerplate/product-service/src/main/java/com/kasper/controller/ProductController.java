package com.kasper.controller;

import com.kasper.dto.ProductDto;
import com.kasper.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    // Simple endpoint to be called by order-service
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        log.info("Received request for product with ID: {}", id);
        try {
            // Simulate fetching a product
            if (id <= 0) {
                throw new IllegalArgumentException("Product ID must be positive.");
            }

            // In a real app, this would come from a service/database
            ProductDto product = new ProductDto(id, "Sample Product " + id, BigDecimal.valueOf(id * 10.5));

            ApiResponse<ProductDto> response = ApiResponse.success("Product fetched successfully", product);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Relying on GlobalExceptionHandler, but could also handle specific cases here
            log.error("Error fetching product with ID {}: {}", id, e.getMessage());
            // Example of re-throwing for the handler:
            if (e instanceof IllegalArgumentException) {
                throw e; // Let GlobalExceptionHandler handle it as BAD_REQUEST
            }
            throw new RuntimeException("Internal error fetching product", e); // Generic fallback
        }
    }
} 