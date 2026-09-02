package com.stepflow.sample.order.dto;

public class OrderDtos {
    public record CreateOrderRequest(String clientRequestId, String customerId) {}
    public record DuplicateOrderResponse(String code, String message, int status, OrderResponse existingOrder) {}
    public record CancelOrderRequest(String orderId, String reason) {}
    public record CompleteOrderRequest(String orderId, String paymentCaptureId, String shipmentId, String notificationId) {}
    public record OrderResponse(String orderId, String clientRequestId, String customerId, String status) {}
    public record StatusResponse(String orderId, String status) {}
    public record CompleteOrderResponse(String orderId, String paymentCaptureId, String shipmentId, String notificationId, String status) {}
}
