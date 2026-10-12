package com.stepflow.order.domain.client.dto;

/** Values exposed by the Order HTTP contract, independent of persistence entities. */
public final class OrderEnums {
    private OrderEnums() {}

    public enum OrderStatus {CREATED, PENDING_PAYMENT, CONFIRMED, PROCESSING, COMPLETED, CANCELLED, FAILED}
    public enum AddressType {SHIPPING, BILLING}
}
