package com.kasper.client;

import com.kasper.dto.PaymentStatusDto;
import com.kasper.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Define Feign client for payment-service
// "payment-service" is the application name registered with Eureka
@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    // Maps to the endpoint in PaymentController
    @GetMapping("/api/payments/status/{orderId}")
    ResponseEntity<ApiResponse<PaymentStatusDto>> getPaymentStatus(@PathVariable("orderId") String orderId);
    // Note: We expect the exact same response structure (ResponseEntity<ApiResponse<PaymentStatusDto>>)
    // as the target endpoint for Feign to deserialize correctly.
} 