package com.kasper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private PaymentProcessor paymentProcessor;

    @Autowired
    public PaymentService(@Qualifier("paypalPaymentProcessor") PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public void pay(double amount) {
        paymentProcessor.processPayment(amount);
    }
}
