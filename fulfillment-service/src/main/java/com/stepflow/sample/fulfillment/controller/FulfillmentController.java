package com.stepflow.sample.fulfillment.controller;

import com.stepflow.sample.fulfillment.dto.FulfillmentDtos.*;
import com.stepflow.sample.fulfillment.service.FulfillmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fulfillment/requests")
public class FulfillmentController {
    private final FulfillmentService service;

    public FulfillmentController(FulfillmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FulfillmentResponse> create(@RequestBody CreateFulfillmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/{fulfillmentId}/cancel")
    public CancelFulfillmentResponse cancel(@PathVariable String fulfillmentId) {
        return service.cancel(fulfillmentId);
    }
}
