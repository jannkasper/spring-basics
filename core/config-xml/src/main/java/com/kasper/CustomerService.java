package com.kasper;

import java.util.Map;

public class CustomerService {
    private Map<String, Customer> customers;

    public void setCustomers(Map<String, Customer> customers) {
        this.customers = customers;
    }

    public void showAllCustomers() {
        System.out.println("Registered Customers:");
        customers.forEach((id, customer) -> {
            System.out.println("ID: " + id);
            customer.showCustomerInfo();
        });
    }
}
