package com.kasper;

public class OrderProcessor {
    private OrderService orderService;
    private CustomerService customerService;
    private PaymentService paymentService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processOrder(String productName, String customerId, double amount) {
        System.out.println("Initiating order processing...");

        orderService.placeOrder(productName);
        paymentService.processPayment(customerId, amount);

        System.out.println("Order completed for customer ID: " + customerId);
    }
}
