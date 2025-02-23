package com.kasper;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public void processPayment(String customerName, double amount) {
        System.out.println("Processing payment of $" + amount + " for customer: " + customerName);
    }
}

