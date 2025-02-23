package com.kasper;

public class ScopedBean {
    public ScopedBean() {
        System.out.println("🔹 ScopedBean Created: " + this + " in Thread: " + Thread.currentThread().getName());
    }

    public void execute() {
        System.out.println("🔸 Executing ScopedBean in Thread: " + Thread.currentThread().getName());
    }
}
