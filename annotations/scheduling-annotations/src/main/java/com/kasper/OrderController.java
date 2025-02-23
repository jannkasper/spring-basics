package com.kasper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessingService orderProcessingService;

    public OrderController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping("/{orderId}/process")
    public String processOrder(@PathVariable String orderId) {
        orderProcessingService.processOrder(orderId);
        return "Order processing started for ID: " + orderId;
    }
}
