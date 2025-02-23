package com.kasper;

import java.util.List;

public class ProductService {
    private List<Product> products;

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

