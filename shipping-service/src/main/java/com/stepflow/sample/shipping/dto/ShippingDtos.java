package com.stepflow.sample.shipping.dto;

public class ShippingDtos {
    public record ShippingOptionsResponse(String recommendedCarrierCode, String recommendedServiceLevel) {}
    public record CreateShipmentRequest(String orderId, String fulfillmentId, String carrierCode, String serviceLevel) {}
    public record ShipmentResponse(String shipmentId, String trackingNumber, String status) {}
    public record CancelShipmentResponse(String shipmentId, String status) {}
}
