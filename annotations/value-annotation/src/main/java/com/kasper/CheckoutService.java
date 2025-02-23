package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    @Value("${checkout.discount.percentage:5}") // Default is 5% if not found
    private double discountPercentage;

    @Value("${checkout.tax.percentage:12}") // Default tax is 12% if not found
    private double taxPercentage;

    public void checkout(double orderTotal) {
        double discount = (orderTotal * discountPercentage) / 100;
        double tax = (orderTotal * taxPercentage) / 100;
        double finalAmount = orderTotal - discount + tax;

        System.out.println("Order Total: $" + orderTotal);
        System.out.println("Discount (" + discountPercentage + "%): -$" + discount);
        System.out.println("Tax (" + taxPercentage + "%): +$" + tax);
        System.out.println("Final Amount: $" + finalAmount);
    }
}

