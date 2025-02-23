package com.kasper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerService {
    private Map<String, Customer> customers;


    /* Constructor Injection */
    public CustomerService(Map<String, Customer> customers) {
        this.customers = customers;
    }

    /* Setter Injection */
//    @Autowired
//    public void setCustomers(Map<String, Customer> customers) {
//        this.customers = customers;
//    }

    public void showAllCustomers() {
        System.out.println("Registered Customers:");
        customers.forEach((id, customer) -> {
            System.out.println("ID: " + id);
            customer.showCustomerInfo();
        });
    }
}
