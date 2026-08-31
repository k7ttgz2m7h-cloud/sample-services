package com.stepflow.sample.shipping.controller;

import com.stepflow.sample.shipping.dto.ShippingDtos.*;
import com.stepflow.sample.shipping.service.ShippingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
public class ShippingController {
    private final ShippingService service;

    public ShippingController(ShippingService service) {
        this.service = service;
    }

    @GetMapping("/options")
    public ShippingOptionsResponse options() {
        return service.options();
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(@RequestBody CreateShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/{shipmentId}/cancel")
    public CancelShipmentResponse cancel(@PathVariable String shipmentId) {
        return service.cancel(shipmentId);
    }
}
