package com.stepflow.customer.domain.client.dto;

import java.util.UUID;

public record CustomerVerificationRequest(UUID customerId, String orderId, String status) {
}
