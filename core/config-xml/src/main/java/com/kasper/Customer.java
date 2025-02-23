package com.kasper;

public class Customer {
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void showCustomerInfo() {
        System.out.println("Customer: " + name + " | Email: " + email);
    }
}
