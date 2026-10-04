package com.stepflow.customer.domain.client.dto;

import java.util.UUID;

public record CustomerVerificationResponse(UUID customerId, boolean verified) {
}
