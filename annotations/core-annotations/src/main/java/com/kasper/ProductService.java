package com.kasper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")  // A new instance is created every time
public class ProductService {

    @Value("${product.defaultName:Generic Product}")  // Injecting default value
    private String defaultProductName;

    public void displayProduct() {
        System.out.println("Product Name: " + defaultProductName);
    }
}
