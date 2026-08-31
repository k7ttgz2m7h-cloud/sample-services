package com.stepflow.sample.order.service;

import com.stepflow.sample.order.dto.OrderDtos.*;
import com.stepflow.sample.order.entity.OrderRecord;
import com.stepflow.sample.order.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse create(CreateOrderRequest request) {
        if (request.clientRequestId() == null || request.clientRequestId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientRequestId is required");
        }
        if (request.customerId() == null || request.customerId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
        }
        var existingOrder = repository.findByClientRequestId(request.clientRequestId());
        if (existingOrder.isPresent()) {
            return toResponse(existingOrder.get());
        }
        String orderId = newOrderId();
        OrderRecord order = new OrderRecord();
        order.setOrderId(orderId);
        order.setClientRequestId(request.clientRequestId());
        order.setCustomerId(request.customerId());
        if (order.getStatus() == null || Boolean.TRUE.equals(order.getCompensated())) {
            order.setStatus("CREATED");
            order.setCompensated(false);
            order.setCompensatedAt(null);
            order.setCompensationReason(null);
        }
        repository.save(order);
        return toResponse(order);
    }

    private String newOrderId() {
        String orderId;
        do {
            orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.findByOrderId(orderId).isPresent());
        return orderId;
    }

    private OrderResponse toResponse(OrderRecord order) {
        return new OrderResponse(order.getOrderId(), order.getClientRequestId(), order.getCustomerId(), order.getStatus());
    }

    public StatusResponse cancel(CancelOrderRequest request) {
        OrderRecord order = repository.findByOrderId(request.orderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + request.orderId()));
        if (!Boolean.TRUE.equals(order.getCompensated())) {
            order.setStatus("CANCELLED");
            order.setCompensated(true);
            order.setCompensatedAt(LocalDateTime.now());
            order.setCompensationReason(request.reason());
        }
        repository.save(order);
        return new StatusResponse(order.getOrderId(), order.getStatus());
    }

    public CompleteOrderResponse complete(CompleteOrderRequest request) {
        OrderRecord order = repository.findByOrderId(request.orderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + request.orderId()));
        order.setPaymentCaptureId(request.paymentCaptureId());
        order.setShipmentId(request.shipmentId());
        order.setNotificationId(request.notificationId());
        order.setStatus("COMPLETED");
        repository.save(order);
        return new CompleteOrderResponse(order.getOrderId(), order.getPaymentCaptureId(), order.getShipmentId(), order.getNotificationId(), order.getStatus());
    }
}
