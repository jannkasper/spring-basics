package com.kasper.controller;

import com.kasper.dto.ProductDto;
import com.kasper.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import com.kasper.client.PaymentServiceClient;
import com.kasper.dto.PaymentStatusDto;
import feign.FeignException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    // Inject the Feign Client
    @Autowired
    private PaymentServiceClient paymentServiceClient;

    // --- DiscoveryClient Example --- //

    @GetMapping("/product-info/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductInfoViaDiscovery(@PathVariable Long productId) {
        log.info("Attempting to get product info for ID: {} using DiscoveryClient", productId);
        try {
            // 1. Discover instances of product-service
            List<ServiceInstance> instances = discoveryClient.getInstances("product-service");
            if (instances == null || instances.isEmpty()) {
                log.error("No instances found for product-service");
                throw new IllegalStateException("product-service not available");
            }

            // 2. Choose an instance (basic approach: take the first one)
            // In a real scenario, use a load balancer (like Spring Cloud LoadBalancer)
            ServiceInstance productServiceInstance = instances.get(0);
            String productServiceUrl = productServiceInstance.getUri().toString();
            log.info("Found product-service instance at: {}", productServiceUrl);

            // 3. Construct the full URL to the target endpoint
            String url = productServiceUrl + "/api/products/" + productId;

            // 4. Make the REST call using RestTemplate
            // Define the expected response type (ApiResponse<ProductDto>)
            ParameterizedTypeReference<ApiResponse<ProductDto>> responseType =
                    new ParameterizedTypeReference<ApiResponse<ProductDto>>() {};

            ResponseEntity<ApiResponse<ProductDto>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, // No request body for GET
                    responseType
            );

            // Check if the call was successful and the body is present
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                ApiResponse<ProductDto> apiResponse = responseEntity.getBody();
                if ("SUCCESS".equals(apiResponse.getResult()) && apiResponse.getData() != null) {
                     log.info("Successfully retrieved product info: {}", apiResponse.getData());
                     // Return the successful response directly
                     return new ResponseEntity<>(apiResponse, HttpStatus.OK);
                } else {
                    log.error("Product service returned an error or no data: {}", apiResponse.getMessage());
                    // Propagate the error message from the downstream service
                    return new ResponseEntity<>(ApiResponse.error("Failed to get product info: " + apiResponse.getMessage()), responseEntity.getStatusCode());
                }
            } else {
                log.error("Received non-successful status code {} from product-service", responseEntity.getStatusCode());
                return new ResponseEntity<>(ApiResponse.error("Failed to get product info from product-service"), responseEntity.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error calling product-service via RestTemplate: {}", e.getMessage());
            // Let GlobalExceptionHandler handle RestClientException
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getProductInfoViaDiscovery: {}", e.getMessage());
            // Let GlobalExceptionHandler handle other exceptions
            throw e;
        }
    }

    // --- Feign Client Example --- //

    @GetMapping("/payment-status/{orderId}")
    public ResponseEntity<ApiResponse<PaymentStatusDto>> getPaymentStatusViaFeign(@PathVariable String orderId) {
        log.info("Attempting to get payment status for order ID: {} using Feign Client", orderId);
        try {
            // Call the payment-service using the Feign client
            ResponseEntity<ApiResponse<PaymentStatusDto>> responseEntity = paymentServiceClient.getPaymentStatus(orderId);

            // Check if the call was successful and the body is present
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                 ApiResponse<PaymentStatusDto> apiResponse = responseEntity.getBody();
                 if ("SUCCESS".equals(apiResponse.getResult()) && apiResponse.getData() != null) {
                     log.info("Successfully retrieved payment status: {}", apiResponse.getData());
                     // Return the successful response directly
                     return new ResponseEntity<>(apiResponse, HttpStatus.OK);
                 } else {
                    log.error("Payment service returned an error or no data: {}", apiResponse.getMessage());
                    // Propagate the error message from the downstream service
                    return new ResponseEntity<>(ApiResponse.error("Failed to get payment status: " + apiResponse.getMessage()), responseEntity.getStatusCode());
                 }
            } else {
                log.error("Received non-successful status code {} from payment-service via Feign", responseEntity.getStatusCode());
                return new ResponseEntity<>(ApiResponse.error("Failed to get payment status from payment-service"), responseEntity.getStatusCode());
            }

        } catch (FeignException e) {
            log.error("Error calling payment-service via Feign: Status={}, Message={}", e.status(), e.getMessage());
            // Let GlobalExceptionHandler handle FeignException
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getPaymentStatusViaFeign: {}", e.getMessage());
            // Let GlobalExceptionHandler handle other exceptions
            throw e;
        }
    }
} 