package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ValueAnnotationApplication {

	public static void main(String[] args) {
		ApplicationContext context =  SpringApplication.run(ValueAnnotationApplication.class, args);

		ProductService productService = context.getBean(ProductService.class);
		productService.displayProduct();

		PaymentService paymentService = context.getBean(PaymentService.class);
		paymentService.processPayment("paypal");

		CheckoutService checkoutService = context.getBean(CheckoutService.class);
		checkoutService.checkout(100);

		CurrencyService currencyService = context.getBean(CurrencyService.class);
		currencyService.displayCurrencies();

		PricingService pricingService = context.getBean(PricingService.class);
		pricingService.displayPricing();


	}

}
