package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class BeanLifecycleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeanLifecycleApplication.class, args);

		System.out.println("🔵 Starting Spring Context...");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

		LifecycleBean bean = context.getBean(LifecycleBean.class);
		bean.doSomething();

		System.out.println("🔴 Closing Spring Context...");
		context.close();
	}

}
