package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${payment.gateway.paypal.url}")
    private String paypalUrl;

    @Value("${payment.gateway.stripe.url}")
    private String stripeUrl;

    public void processPayment(String gateway) {
        if ("paypal".equalsIgnoreCase(gateway)) {
            System.out.println("Processing via PayPal: " + paypalUrl);
        } else if ("stripe".equalsIgnoreCase(gateway)) {
            System.out.println("Processing via Stripe: " + stripeUrl);
        } else {
            System.out.println("Unsupported payment gateway.");
        }
    }
}

