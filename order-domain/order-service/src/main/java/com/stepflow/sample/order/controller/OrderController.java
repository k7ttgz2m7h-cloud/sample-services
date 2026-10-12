package com.stepflow.sample.order.controller;

import com.stepflow.sample.order.dto.OrderRequests.*;
import com.stepflow.sample.order.dto.OrderResponses.OrderResponse;
import com.stepflow.sample.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.stepflow.sample.order.entity.OrderEnums.AddressType;

@RestController
@RequestMapping({"/orders", "/api/orders"})
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request, idempotencyKey, changedBy));
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId) {
        return orderService.get(orderId);
    }

    @PutMapping("/{orderId}")
    public OrderResponse update(@PathVariable UUID orderId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody UpdateOrderRequest request) {
        return orderService.update(orderId, request, changedBy);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItem(@PathVariable UUID orderId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody AddOrderItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addItem(orderId, request, changedBy));
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    public OrderResponse updateItem(@PathVariable UUID orderId, @PathVariable UUID orderItemId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody UpdateOrderItemRequest request) {
        return orderService.updateItem(orderId, orderItemId, request, changedBy);
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    public OrderResponse removeItem(@PathVariable UUID orderId, @PathVariable UUID orderItemId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return orderService.removeItem(orderId, orderItemId, changedBy);
    }

    @PutMapping("/{orderId}/addresses/{addressType}")
    public OrderResponse updateAddress(@PathVariable UUID orderId, @PathVariable AddressType addressType, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody UpdateOrderAddressRequest request) {
        return orderService.updateAddress(orderId, addressType, request, changedBy);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable UUID orderId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(orderId, request, changedBy);
    }

    @PostMapping("/{orderId}/cancel")
    public OrderResponse cancel(@PathVariable UUID orderId, @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy, @Valid @RequestBody CancelOrderRequest request) {
        return orderService.cancel(orderId, request, changedBy);
    }
}
