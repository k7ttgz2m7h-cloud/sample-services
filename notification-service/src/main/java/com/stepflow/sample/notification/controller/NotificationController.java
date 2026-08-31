package com.stepflow.sample.notification.controller;

import com.stepflow.sample.notification.dto.NotificationDtos.*;
import com.stepflow.sample.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/email")
    public ResponseEntity<EmailNotificationResponse> send(@RequestBody EmailNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.send(request));
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendGeneric(@RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.sendGeneric(request));
    }
}
