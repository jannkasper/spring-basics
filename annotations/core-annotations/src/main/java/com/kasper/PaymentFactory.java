package com.kasper;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class PaymentFactory {

    @Lookup
    public abstract PaymentProcessor getPaymentProcessor();

    public void processDynamicPayment(double amount) {
        PaymentProcessor paymentProcessor = getPaymentProcessor();
        paymentProcessor.processPayment(amount);
    }
}
