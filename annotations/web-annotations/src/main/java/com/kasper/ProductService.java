package com.kasper;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    private final Map<Integer, Product> products = new HashMap<>();

    public ProductService() {
        products.put(1, new Product(1, "Laptop", 1200));
        products.put(2, new Product(2, "Smartphone", 800));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(int id) {
        return products.get(id);
    }

    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    public void updateProduct(int id, Product product) {
        products.put(id, product);
    }

    public void deleteProduct(int id) {
        products.remove(id);
    }
}
