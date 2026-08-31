package com.stepflow.sample.notification.dto;

import java.util.List;

public class NotificationDtos {
    public record EmailNotificationRequest(String orderId, String customerId) {}
    public record EmailNotificationResponse(String notificationId, String orderId, String status) {}
    public record NotificationRequest(
            String orderId,
            String customerId,
            String phase,
            String mode,
            List<String> emailIds,
            List<String> phoneNumbers,
            String subject,
            String message
    ) {}
    public record NotificationResponse(String notificationId, String orderId, String phase, String mode, String status) {}
}
