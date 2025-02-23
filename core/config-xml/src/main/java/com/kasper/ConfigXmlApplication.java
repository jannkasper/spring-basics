package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class ConfigXmlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigXmlApplication.class, args);

		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		// Retrieve and display available products
		ProductService productService = (ProductService) context.getBean("productService");
		productService.showAllProducts();

		System.out.println("----------------------------------");

		// Retrieve and display registered customers
		CustomerService customerService = (CustomerService) context.getBean("customerService");
		customerService.showAllCustomers();

		System.out.println("----------------------------------");

		// Process an order
		OrderProcessor orderProcessor = (OrderProcessor) context.getBean("orderProcessor");
		orderProcessor.processOrder("Laptop", "C001", 1200.00);

		((ClassPathXmlApplicationContext) context).close();
	}

}
