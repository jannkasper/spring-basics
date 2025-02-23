package com.kasper;

import org.springframework.stereotype.Component;

@Component
public class OrderProcessor {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public OrderProcessor(OrderService orderService, CustomerService customerService, PaymentService paymentService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.paymentService = paymentService;
    }

    public void processOrder(String productName, String customerId, double amount) {
        System.out.println("Initiating order processing...");

        orderService.placeOrder(productName);
        paymentService.processPayment(customerId, amount);

        System.out.println("Order completed for customer ID: " + customerId);
    }
}
