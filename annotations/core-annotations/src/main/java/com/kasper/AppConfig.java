package com.kasper;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.kasper")
public class AppConfig {

    @Bean
    @Primary
    public PaymentProcessor stripePaymentProcessor() {
        return new StripePaymentProcessor();
    }

    @Bean
    public PaymentProcessor paypalPaymentProcessor() {
        return new PayPalPaymentProcessor();
    }
}
