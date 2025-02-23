package com.kasper;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount + " via Stripe.");
    }
}
