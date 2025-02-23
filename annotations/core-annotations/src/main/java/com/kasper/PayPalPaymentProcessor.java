package com.kasper;

import org.springframework.stereotype.Component;

@Component
public class PayPalPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount + " via PayPal.");
    }
}
