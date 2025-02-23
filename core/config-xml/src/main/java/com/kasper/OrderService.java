package com.kasper;

public class OrderService {
    private ProductService productService;

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void placeOrder(String productName) {
        System.out.println("Processing order for product: " + productName);
    }
}

