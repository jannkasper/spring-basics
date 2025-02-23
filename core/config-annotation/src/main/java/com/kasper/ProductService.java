package com.kasper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private List<Product> products;

    /* Constructor Injection */
//    public ProductService(List<Product> products) {
//        this.products = products;
//    }

    /* Setter Injection */
    @Autowired
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void showAllProducts() {
        System.out.println("Available Products:");
        for (Product product : products) {
            product.showProductInfo();
        }
    }
}

