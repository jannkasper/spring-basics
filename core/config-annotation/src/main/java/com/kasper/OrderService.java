package com.kasper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private ProductService productService;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    public void placeOrder(String productName) {
        System.out.println("Processing order for product: " + productName);
    }
}

