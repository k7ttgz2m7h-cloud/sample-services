package com.stepflow.sample.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class OrderRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String clientRequestId;
    private String customerId;
    private String status;
    private String paymentCaptureId;
    private String shipmentId;
    private String notificationId;
    private Boolean compensated = false;
    private LocalDateTime compensatedAt;
    private String compensationReason;
    private String compensationExecutionId;

    public Long getId() { return id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentCaptureId() { return paymentCaptureId; }
    public void setPaymentCaptureId(String paymentCaptureId) { this.paymentCaptureId = paymentCaptureId; }
    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public Boolean getCompensated() { return compensated; }
    public void setCompensated(Boolean compensated) { this.compensated = compensated; }
    public LocalDateTime getCompensatedAt() { return compensatedAt; }
    public void setCompensatedAt(LocalDateTime compensatedAt) { this.compensatedAt = compensatedAt; }
    public String getCompensationReason() { return compensationReason; }
    public void setCompensationReason(String compensationReason) { this.compensationReason = compensationReason; }
    public String getCompensationExecutionId() { return compensationExecutionId; }
    public void setCompensationExecutionId(String compensationExecutionId) { this.compensationExecutionId = compensationExecutionId; }
}
