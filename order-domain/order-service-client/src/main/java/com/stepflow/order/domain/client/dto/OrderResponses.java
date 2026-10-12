package com.stepflow.order.domain.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.stepflow.order.domain.client.dto.OrderEnums.*;

public final class OrderResponses {
    private OrderResponses() {
    }

    public record OrderItemResponse(UUID orderItemId, UUID productId, String sku, String productName, int quantity,
                                    BigDecimal unitPrice, BigDecimal discountAmount, BigDecimal taxAmount,
                                    BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record OrderAddressResponse(UUID orderAddressId, UUID customerAddressId, AddressType addressType,
                                       String addressLine1, String addressLine2, String city, String state,
                                       String postalCode, String countryCode, LocalDateTime createdAt) {
    }

    public record OrderResponse(UUID orderId, UUID customerId, OrderStatus orderStatus, String currencyCode,
                                BigDecimal subtotalAmount, BigDecimal discountAmount, BigDecimal taxAmount,
                                BigDecimal shippingAmount, BigDecimal totalAmount, List<OrderItemResponse> items,
                                OrderAddressResponse shippingAddress, OrderAddressResponse billingAddress,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record ApiError(String code, String message, int status, Map<String, String> fieldErrors) {
    }
}
