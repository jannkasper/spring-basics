package com.kasper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class LifecycleBean implements InitializingBean, DisposableBean {

    public LifecycleBean() {
        System.out.println("1️⃣ Constructor: Bean is being created...");
    }

    // Using @PostConstruct
    @PostConstruct
    public void postConstruct() {
        System.out.println("2️⃣ @PostConstruct: Bean is initialized...");
    }

    // Using InitializingBean interface
    @Override
    public void afterPropertiesSet() {
        System.out.println("3️⃣ afterPropertiesSet(): Properties are set...");
    }

    // Custom Init Method
    public void customInit() {
        System.out.println("4️⃣ customInit(): Custom init method called...");
    }

    // Business Logic
    public void doSomething() {
        System.out.println("✅ Business Logic: Executing some work...");
    }

    // Using @PreDestroy
    @PreDestroy
    public void preDestroy() {
        System.out.println("5️⃣ @PreDestroy: Bean is about to be destroyed...");
    }

    // Using DisposableBean Interface
    @Override
    public void destroy() {
        System.out.println("6️⃣ destroy(): Cleanup before garbage collection...");
    }

    // Custom Destroy Method
    public void customDestroy() {
        System.out.println("7️⃣ customDestroy(): Custom destroy method called...");
    }
}
