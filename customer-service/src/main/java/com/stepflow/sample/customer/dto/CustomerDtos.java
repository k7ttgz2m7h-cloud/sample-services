package com.stepflow.sample.customer.dto;

public class CustomerDtos {
    public record VerifyCustomerRequest(String customerId, String orderId, String status) {}
    public record VerifyCustomerResponse(String customerId, boolean verified) {}
}
