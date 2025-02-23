package com.kasper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class BeanScopeApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(BeanScopeApplication.class, args);

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);


		Runnable task = () -> {
			ScopedBean bean = context.getBean(ScopedBean.class);
			bean.execute();
		};

		// Running in main thread
		System.out.println("🟢 Main Thread:");
		task.run();
		System.out.println("\n🟢 Main Thread:");
		task.run();

		// Running in new threads
		System.out.println("\n🔵 New Threads:");
		Thread t1 = new Thread(task);
		Thread t2 = new Thread(task);

		t1.start();
		t2.start();

		t1.join();
		t2.join();

		context.close();
	}

}
