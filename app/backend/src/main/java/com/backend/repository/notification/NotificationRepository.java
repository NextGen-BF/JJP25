package com.backend.repository.notification;

import com.backend.entity.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_IdOrderBySentAtDesc(Long userId);
}