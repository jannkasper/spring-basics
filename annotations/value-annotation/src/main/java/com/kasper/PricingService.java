package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    @Value("${product.default.price}")
    private double basePrice;

    @Value("#{${product.default.price} - (${product.default.price} * 10 / 100)}")
    private double discountedPrice;

    public void displayPricing() {
        System.out.println("Base Price: $" + basePrice);
        System.out.println("Discounted Price (10% Off): $" + discountedPrice);
    }
}

