package com.stepflow.sample.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.OrderStatus;

public final class OrderRequests {
    private OrderRequests() {
    }

    public record CreateOrderItemRequest(UUID productId, @NotBlank @Size(max = 100) String sku,
                                         @NotBlank @Size(max = 255) String productName, @Positive int quantity,
                                         @NotNull @PositiveOrZero BigDecimal unitPrice,
                                         @NotNull @PositiveOrZero BigDecimal discountAmount,
                                         @NotNull @PositiveOrZero BigDecimal taxAmount) {
    }

    public record AddOrderItemRequest(UUID productId, @NotBlank @Size(max = 100) String sku,
                                      @NotBlank @Size(max = 255) String productName, @Positive int quantity,
                                      @NotNull @PositiveOrZero BigDecimal unitPrice,
                                      @NotNull @PositiveOrZero BigDecimal discountAmount,
                                      @NotNull @PositiveOrZero BigDecimal taxAmount) {
    }

    public record UpdateOrderItemRequest(UUID productId, @NotBlank @Size(max = 100) String sku,
                                         @NotBlank @Size(max = 255) String productName, @Positive int quantity,
                                         @NotNull @PositiveOrZero BigDecimal unitPrice,
                                         @NotNull @PositiveOrZero BigDecimal discountAmount,
                                         @NotNull @PositiveOrZero BigDecimal taxAmount) {
    }

    public record CreateOrderAddressRequest(UUID customerAddressId, @NotBlank @Size(max = 200) String addressLine1,
                                            @Size(max = 200) String addressLine2,
                                            @NotBlank @Size(max = 100) String city,
                                            @NotBlank @Size(max = 100) String state,
                                            @NotBlank @Size(max = 30) String postalCode,
                                            @NotBlank @Size(min = 2, max = 2) String countryCode) {
    }

    public record UpdateOrderAddressRequest(UUID customerAddressId, @NotBlank @Size(max = 200) String addressLine1,
                                            @Size(max = 200) String addressLine2,
                                            @NotBlank @Size(max = 100) String city,
                                            @NotBlank @Size(max = 100) String state,
                                            @NotBlank @Size(max = 30) String postalCode,
                                            @NotBlank @Size(min = 2, max = 2) String countryCode) {
    }

    public record CreateOrderRequest(@NotNull UUID customerId, @NotBlank @Size(min = 3, max = 3) String currencyCode,
                                     @NotEmpty List<@Valid CreateOrderItemRequest> items,
                                     @NotNull @Valid CreateOrderAddressRequest shippingAddress,
                                     @Valid CreateOrderAddressRequest billingAddress,
                                     @NotNull @PositiveOrZero BigDecimal shippingAmount) {
    }

    public record UpdateOrderRequest(@NotBlank @Size(min = 3, max = 3) String currencyCode,
                                     @NotNull @PositiveOrZero BigDecimal shippingAmount) {
    }

    public record UpdateOrderStatusRequest(@NotNull OrderStatus newStatus, @Size(max = 500) String changeReason) {
    }

    public record CancelOrderRequest(@NotBlank @Size(max = 500) String reason) {
    }
}
