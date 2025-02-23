package com.kasper;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    @Async
    public void processOrder(String orderId) {
        System.out.println("🛒 Processing Order: " + orderId + " - Thread: " + Thread.currentThread().getName());

        try {
            Thread.sleep(5000); // Simulate payment processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Order " + orderId + " processed successfully.");
    }
}
