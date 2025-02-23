package com.kasper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Product {

    private int id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Min(value = 1, message = "Price must be greater than 0")
    private double price;

    public Product() {}

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
