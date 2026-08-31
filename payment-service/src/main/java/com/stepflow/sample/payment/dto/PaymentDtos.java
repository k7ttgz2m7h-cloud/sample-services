package com.stepflow.sample.payment.dto;

import java.math.BigDecimal;

public class PaymentDtos {
    public record AuthorizeRequest(String orderId, BigDecimal amount) {}
    public record VoidRequest(String authorizationId, String orderId) {}
    public record CaptureRequest(String orderId, String authorizationId, BigDecimal amount) {}
    public record RefundRequest(String captureId, String orderId, BigDecimal amount) {}
    public record PaymentResponse(String authorizationId, String captureId, String orderId, String status) {}
}
