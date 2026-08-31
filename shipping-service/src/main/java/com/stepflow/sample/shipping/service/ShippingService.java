package com.stepflow.sample.shipping.service;

import com.stepflow.sample.shipping.dto.ShippingDtos.*;
import com.stepflow.sample.shipping.entity.Shipment;
import com.stepflow.sample.shipping.repository.ShipmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ShippingService {
    private final ShipmentRepository repository;

    public ShippingService(ShipmentRepository repository) {
        this.repository = repository;
    }

    public ShippingOptionsResponse options() {
        return new ShippingOptionsResponse("UPS", "GROUND");
    }

    public ShipmentResponse create(CreateShipmentRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (repository.findByOrderId(request.orderId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Shipment already exists for order: " + request.orderId());
        }
        Shipment shipment = new Shipment();
        shipment.setShipmentId("SHIP-" + request.orderId());
        shipment.setTrackingNumber("TRACK-" + request.orderId());
        shipment.setOrderId(request.orderId());
        shipment.setFulfillmentId(request.fulfillmentId());
        shipment.setCarrierCode(request.carrierCode());
        shipment.setServiceLevel(request.serviceLevel());
        if (shipment.getStatus() == null || Boolean.TRUE.equals(shipment.getCompensated())) {
            shipment.setStatus("CREATED");
            shipment.setCompensated(false);
            shipment.setCompensatedAt(null);
            shipment.setCompensationReason(null);
        }
        repository.save(shipment);
        return new ShipmentResponse(shipment.getShipmentId(), shipment.getTrackingNumber(), shipment.getStatus());
    }

    public CancelShipmentResponse cancel(String shipmentId) {
        Shipment shipment = repository.findByShipmentId(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipment not found: " + shipmentId));
        if (!Boolean.TRUE.equals(shipment.getCompensated())) {
            shipment.setStatus("CANCELLED");
            shipment.setCompensated(true);
            shipment.setCompensatedAt(LocalDateTime.now());
            shipment.setCompensationReason("BUSINESS_PROCESS_COMPENSATION");
        }
        repository.save(shipment);
        return new CancelShipmentResponse(shipment.getShipmentId(), shipment.getStatus());
    }
}
