package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Value("${product.default.name}")
    private String productName;

    @Value("${product.default.price}")
    private double price;

    public void displayProduct() {
        System.out.println("Product Name: " + productName);
        System.out.println("Product Price: $" + price);
    }
}

