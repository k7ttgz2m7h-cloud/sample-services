package com.stepflow.order.domain.client;

import com.stepflow.order.domain.client.dto.OrderRequests.*;
import com.stepflow.order.domain.client.dto.OrderResponses.OrderResponse;
import com.stepflow.order.domain.client.dto.OrderEnums.AddressType;

import java.util.UUID;

/**
 * Synchronous Order API. Null changedBy uses the server's default audit actor.
 */
public interface OrderClient {
    OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey, String changedBy);

    OrderResponse getOrder(UUID orderId);

    OrderResponse updateOrder(UUID orderId, UpdateOrderRequest request, String changedBy);

    OrderResponse addItem(UUID orderId, AddOrderItemRequest request, String changedBy);

    OrderResponse updateItem(UUID orderId, UUID orderItemId, UpdateOrderItemRequest request, String changedBy);

    OrderResponse removeItem(UUID orderId, UUID orderItemId, String changedBy);

    OrderResponse updateAddress(UUID orderId, AddressType addressType, UpdateOrderAddressRequest request, String changedBy);

    OrderResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request, String changedBy);

    OrderResponse cancelOrder(UUID orderId, CancelOrderRequest request, String changedBy);
}
