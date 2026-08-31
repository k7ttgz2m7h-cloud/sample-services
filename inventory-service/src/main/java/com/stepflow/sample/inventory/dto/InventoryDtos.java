package com.stepflow.sample.inventory.dto;

import java.util.List;

public class InventoryDtos {
    public record Item(String productId, Integer quantity) {}
    public record ReservationRequest(String orderId, List<Item> items) {}
    public record ReservationResponse(String reservationId, String orderId, String status) {}
    public record ReleaseResponse(String reservationId, String status) {}
}
