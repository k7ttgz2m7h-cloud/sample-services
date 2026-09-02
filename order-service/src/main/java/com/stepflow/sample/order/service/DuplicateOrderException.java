package com.stepflow.sample.order.service;

import com.stepflow.sample.order.dto.OrderDtos.OrderResponse;

public class DuplicateOrderException extends RuntimeException {
    private final OrderResponse existingOrder;

    public DuplicateOrderException(OrderResponse existingOrder) {
        super("Order already exists for clientRequestId: " + existingOrder.clientRequestId());
        this.existingOrder = existingOrder;
    }

    public OrderResponse getExistingOrder() {
        return existingOrder;
    }
}
