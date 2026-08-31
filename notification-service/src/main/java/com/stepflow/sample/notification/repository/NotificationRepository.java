package com.stepflow.sample.notification.repository;

import com.stepflow.sample.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
