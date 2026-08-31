package com.stepflow.sample.order.controller;

import com.stepflow.sample.order.dto.OrderDtos.*;
import com.stepflow.sample.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/cancel")
    public StatusResponse cancel(@RequestBody CancelOrderRequest request) {
        return service.cancel(request);
    }

    @PostMapping("/complete")
    public CompleteOrderResponse complete(@RequestBody CompleteOrderRequest request) {
        return service.complete(request);
    }
}
