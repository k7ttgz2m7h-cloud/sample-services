package com.stepflow.customer.domain.dto;

import java.util.UUID;

public class CustomerDtos {
    public record VerifyCustomerRequest(UUID customerId, String orderId, String status) {
    }

    public record VerifyCustomerResponse(UUID customerId, boolean verified) {
    }
}
