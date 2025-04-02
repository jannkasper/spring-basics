package com.kasper.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusOrderByTimestampDesc(Notification.NotificationStatus status);
    List<Notification> findByTodoIdOrderByTimestampDesc(String todoId);
    List<Notification> findAllByOrderByTimestampDesc();
}