package com.kasper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Runs every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailySalesReport() {
        System.out.println("📊 Generating Daily Sales Report at " + LocalDateTime.now().format(FORMATTER));
    }

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void checkAbandonedCarts() {
        System.out.println("🛒 Checking for abandoned carts at " + LocalDateTime.now().format(FORMATTER));
    }

    // Runs every 30 minutes
    @Scheduled(fixedDelay = 1800000, initialDelay = 5000)
    public void sendPromotionalEmails() {
        System.out.println("📩 Sending promotional emails at " + LocalDateTime.now().format(FORMATTER));
    }
}
