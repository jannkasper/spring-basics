package com.kasper.controller;

import com.kasper.dto.PaymentStatusDto;
import com.kasper.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    // Simple endpoint to be called by order-service
    @GetMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<PaymentStatusDto>> getPaymentStatus(@PathVariable String orderId) {
        log.info("Received request for payment status for order ID: {}", orderId);
        try {
            // Simulate fetching payment status
            if (orderId == null || orderId.isBlank()) {
                throw new IllegalArgumentException("Order ID cannot be blank.");
            }

            // In a real app, this would come from a service/database
            // Simulate different statuses based on orderId for variety
            String status = (orderId.hashCode() % 2 == 0) ? "PAID" : "PENDING";
            PaymentStatusDto paymentStatus = new PaymentStatusDto(orderId, status, LocalDateTime.now());

            ApiResponse<PaymentStatusDto> response = ApiResponse.success("Payment status fetched successfully", paymentStatus);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error fetching payment status for order ID {}: {}", orderId, e.getMessage());
            if (e instanceof IllegalArgumentException) {
                throw e; // Let GlobalExceptionHandler handle it as BAD_REQUEST
            }
            throw new RuntimeException("Internal error fetching payment status", e); // Generic fallback
        }
    }
} 