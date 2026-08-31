package com.stepflow.sample.fulfillment.dto;

import java.util.Map;

public class FulfillmentDtos {
    public record CreateFulfillmentRequest(String orderId, String reservationId) {}
    public record FulfillmentResponse(String fulfillmentId, String orderId, String status, Map<String, Object> packageDetails) {}
    public record CancelFulfillmentResponse(String fulfillmentId, String status) {}
}
