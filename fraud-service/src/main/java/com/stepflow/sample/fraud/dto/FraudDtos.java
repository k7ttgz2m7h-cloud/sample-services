package com.stepflow.sample.fraud.dto;

import java.math.BigDecimal;

public class FraudDtos {
    public record FraudScreenRequest(String orderId, String customerId, BigDecimal totalAmount) {}
    public record FraudScreenResponse(String orderId, String decision, Integer riskScore) {}
}
