package com.kasper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MultiScheduledTasks {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Schedules({
            @Scheduled(cron = "0 0 2 * * ?"), // Every day at 2 AM
            @Scheduled(fixedRate = 60000) // Every 60 seconds
    })
    public void generateWeeklyReport() {
        System.out.println("📑 Weekly Sales Report generated at " + LocalDateTime.now().format(FORMATTER));
    }
}
