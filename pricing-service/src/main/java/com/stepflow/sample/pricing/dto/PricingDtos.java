package com.stepflow.sample.pricing.dto;

import java.math.BigDecimal;
import java.util.List;

public class PricingDtos {
    public record Item(String productId, Integer quantity) {}
    public record CalculatePriceRequest(String orderId, List<Item> items) {}
    public record CalculatePriceResponse(String orderId, BigDecimal totalAmount, String currency) {}
}
