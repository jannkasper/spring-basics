package com.kasper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CoreAnnotationsApplication {

	public static void main(String[] args) {

		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

		// Payment Service
		PaymentService paymentService = context.getBean(PaymentService.class);
		paymentService.pay(100.00);

		// Product Service
		ProductService productService1 = context.getBean(ProductService.class);
		productService1.displayProduct();

		ProductService productService2 = context.getBean(ProductService.class);
		System.out.println("Are ProductService beans same? " + (productService1 == productService2)); // False due to prototype scope

		// Lazy Service
		LazyService lazyService = context.getBean(LazyService.class);
		lazyService.performLazyOperation();
	}

}
