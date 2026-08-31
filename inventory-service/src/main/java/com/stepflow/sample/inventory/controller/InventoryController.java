package com.stepflow.sample.inventory.controller;

import com.stepflow.sample.inventory.dto.InventoryDtos.*;
import com.stepflow.sample.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> reserve(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.reserve(request));
    }

    @PostMapping("/reservations/{reservationId}/release")
    public ReleaseResponse release(@PathVariable String reservationId) {
        return service.release(reservationId);
    }
}
