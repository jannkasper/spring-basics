package com.kasper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.kasper.model.Message;
import com.kasper.producer.MessageProducer;

@SpringBootApplication
public class KafkaBasicsApplication {

	private static final Logger logger = LoggerFactory.getLogger(KafkaBasicsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(KafkaBasicsApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner demoKafkaMessaging(MessageProducer producer) {
		return args -> {
			// Send a few sample messages when the application starts
			logger.info("Sending sample messages...");
			
			for (int i = 1; i <= 5; i++) {
				Message message = Message.create("Sample message #" + i);
				producer.sendMessage(message);
				Thread.sleep(1000); // Slow down message sending for demo purposes
			}
		};
	}
}
