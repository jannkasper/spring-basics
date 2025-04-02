package com.kasper.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    
    @Transactional
    public Notification createNotification(String todoId, String message, String notificationType) {
        log.info("Creating notification: todoId={}, type={}", todoId, notificationType);
        
        Notification notification = Notification.builder()
                .todoId(todoId)
                .message(message)
                .notificationType(notificationType)
                .status(Notification.NotificationStatus.UNREAD)
                .timestamp(LocalDateTime.now())
                .build();
        
        return repository.save(notification);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        repository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(Notification.NotificationStatus.READ);
            repository.save(notification);
        });
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications() {
        return repository.findByStatusOrderByTimestampDesc(Notification.NotificationStatus.UNREAD);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return repository.findAllByOrderByTimestampDesc();
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForTodo(String todoId) {
        return repository.findByTodoIdOrderByTimestampDesc(todoId);
    }
}