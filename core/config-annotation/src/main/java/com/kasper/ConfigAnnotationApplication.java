package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class ConfigAnnotationApplication {

	public static void main(String[] args) {
//		SpringApplication.run(ConfigAnnotationApplication.class, args);

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		// Retrieve and display available products
		ProductService productService = context.getBean(ProductService.class);
		productService.showAllProducts();

		System.out.println("----------------------------------");

		// Retrieve and display registered customers
		CustomerService customerService = context.getBean(CustomerService.class);
		customerService.showAllCustomers();

		System.out.println("----------------------------------");

		// Process an order
		OrderProcessor orderProcessor = context.getBean(OrderProcessor.class);
		orderProcessor.processOrder("Laptop", "C001", 1200.00);

		((AnnotationConfigApplicationContext) context).close();
	}

}
