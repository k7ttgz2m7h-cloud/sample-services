package com.stepflow.order.domain.client.dto;


import java.math.BigDecimal;
import java.util.*;

import static com.stepflow.order.domain.client.dto.OrderEnums.OrderStatus;

public final class OrderRequests {
    private OrderRequests() {
    }

    public record CreateOrderItemRequest(UUID productId, String sku,
                                         String productName, int quantity,
                                         BigDecimal unitPrice,
                                         BigDecimal discountAmount,
                                         BigDecimal taxAmount) {
    }

    public record AddOrderItemRequest(UUID productId, String sku,
                                      String productName, int quantity,
                                      BigDecimal unitPrice,
                                      BigDecimal discountAmount,
                                      BigDecimal taxAmount) {
    }

    public record UpdateOrderItemRequest(UUID productId, String sku,
                                         String productName, int quantity,
                                         BigDecimal unitPrice,
                                         BigDecimal discountAmount,
                                         BigDecimal taxAmount) {
    }

    public record CreateOrderAddressRequest(UUID customerAddressId, String addressLine1,
                                            String addressLine2,
                                            String city,
                                            String state,
                                            String postalCode,
                                            String countryCode) {
    }

    public record UpdateOrderAddressRequest(UUID customerAddressId, String addressLine1,
                                            String addressLine2,
                                            String city,
                                            String state,
                                            String postalCode,
                                            String countryCode) {
    }

    public record CreateOrderRequest(UUID customerId, String currencyCode,
                                     List<CreateOrderItemRequest> items,
                                     CreateOrderAddressRequest shippingAddress,
                                     CreateOrderAddressRequest billingAddress,
                                     BigDecimal shippingAmount) {
    }

    public record UpdateOrderRequest(String currencyCode,
                                     BigDecimal shippingAmount) {
    }

    public record UpdateOrderStatusRequest(OrderStatus newStatus, String changeReason) {
    }

    public record CancelOrderRequest(String reason) {
    }
}
