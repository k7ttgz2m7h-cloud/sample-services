package com.stepflow.sample.fulfillment.service;

import com.stepflow.sample.fulfillment.dto.FulfillmentDtos.*;
import com.stepflow.sample.fulfillment.entity.Fulfillment;
import com.stepflow.sample.fulfillment.repository.FulfillmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FulfillmentService {
    private final FulfillmentRepository repository;

    public FulfillmentService(FulfillmentRepository repository) {
        this.repository = repository;
    }

    public FulfillmentResponse create(CreateFulfillmentRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (repository.findByOrderId(request.orderId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Fulfillment already exists for order: " + request.orderId());
        }
        Fulfillment fulfillment = new Fulfillment();
        fulfillment.setFulfillmentId("FUL-" + request.orderId());
        fulfillment.setOrderId(request.orderId());
        fulfillment.setReservationId(request.reservationId());
        if (fulfillment.getStatus() == null || Boolean.TRUE.equals(fulfillment.getCompensated())) {
            fulfillment.setStatus("CREATED");
            fulfillment.setCompensated(false);
            fulfillment.setCompensatedAt(null);
            fulfillment.setCompensationReason(null);
        }
        repository.save(fulfillment);
        Map<String, Object> packageDetails = Map.of("weight", 2.0, "length", 10, "width", 8, "height", 5);
        return new FulfillmentResponse(fulfillment.getFulfillmentId(), fulfillment.getOrderId(), fulfillment.getStatus(), packageDetails);
    }

    public CancelFulfillmentResponse cancel(String fulfillmentId) {
        Fulfillment fulfillment = repository.findByFulfillmentId(fulfillmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fulfillment not found: " + fulfillmentId));
        if (!Boolean.TRUE.equals(fulfillment.getCompensated())) {
            fulfillment.setStatus("CANCELLED");
            fulfillment.setCompensated(true);
            fulfillment.setCompensatedAt(LocalDateTime.now());
            fulfillment.setCompensationReason("BUSINESS_PROCESS_COMPENSATION");
        }
        repository.save(fulfillment);
        return new CancelFulfillmentResponse(fulfillment.getFulfillmentId(), fulfillment.getStatus());
    }
}
