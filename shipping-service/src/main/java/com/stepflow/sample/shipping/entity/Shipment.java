package com.stepflow.sample.shipping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shipmentId;
    private String orderId;
    private String fulfillmentId;
    private String trackingNumber;
    private String carrierCode;
    private String serviceLevel;
    private String status;
    private Boolean compensated = false;
    private LocalDateTime compensatedAt;
    private String compensationReason;
    private String compensationExecutionId;

    public String getShipmentId() { return shipmentId; }
    public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getFulfillmentId() { return fulfillmentId; }
    public void setFulfillmentId(String fulfillmentId) { this.fulfillmentId = fulfillmentId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrierCode() { return carrierCode; }
    public void setCarrierCode(String carrierCode) { this.carrierCode = carrierCode; }
    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getCompensated() { return compensated; }
    public void setCompensated(Boolean compensated) { this.compensated = compensated; }
    public LocalDateTime getCompensatedAt() { return compensatedAt; }
    public void setCompensatedAt(LocalDateTime compensatedAt) { this.compensatedAt = compensatedAt; }
    public String getCompensationReason() { return compensationReason; }
    public void setCompensationReason(String compensationReason) { this.compensationReason = compensationReason; }
    public String getCompensationExecutionId() { return compensationExecutionId; }
    public void setCompensationExecutionId(String compensationExecutionId) { this.compensationExecutionId = compensationExecutionId; }
}
