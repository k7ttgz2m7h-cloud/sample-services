package com.stepflow.sample.notification.service;

import com.stepflow.sample.notification.dto.NotificationDtos.*;
import com.stepflow.sample.notification.entity.Notification;
import com.stepflow.sample.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public EmailNotificationResponse send(EmailNotificationRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        Notification notification = new Notification();
        notification.setNotificationId("NOT-1001");
        notification.setOrderId(request.orderId());
        notification.setCustomerId(request.customerId());
        notification.setStatus("SENT");
        repository.save(notification);
        return new EmailNotificationResponse(notification.getNotificationId(), notification.getOrderId(), notification.getStatus());
    }

    public NotificationResponse sendGeneric(NotificationRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (request.mode() == null || request.mode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mode is required");
        }

        Notification notification = new Notification();
        notification.setNotificationId("NOT-" + UUID.randomUUID());
        notification.setOrderId(request.orderId());
        notification.setCustomerId(request.customerId());
        notification.setPhase(request.phase());
        notification.setMode(request.mode());
        notification.setDestination(resolveDestination(request));
        notification.setStatus(notification.getDestination().isBlank() ? "SKIPPED" : "SENT");
        repository.save(notification);
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getOrderId(),
                notification.getPhase(),
                notification.getMode(),
                notification.getStatus()
        );
    }

    private String resolveDestination(NotificationRequest request) {
        String mode = request.mode().toUpperCase();
        if ("SMS".equals(mode)) {
            return join(request.phoneNumbers());
        }
        if ("EMAIL".equals(mode)) {
            return join(request.emailIds());
        }
        return join(request.emailIds()) + "|" + join(request.phoneNumbers());
    }

    private String join(List<String> values) {
        if (values == null) {
            return "";
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }
}
