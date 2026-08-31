package com.stepflow.sample.inventory.service;

import com.stepflow.sample.inventory.dto.InventoryDtos.*;
import com.stepflow.sample.inventory.entity.InventoryReservation;
import com.stepflow.sample.inventory.repository.InventoryReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class InventoryService {
    private final InventoryReservationRepository repository;

    public InventoryService(InventoryReservationRepository repository) {
        this.repository = repository;
    }

    public ReservationResponse reserve(ReservationRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (repository.findByOrderId(request.orderId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inventory reservation already exists for order: " + request.orderId());
        }
        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId("RES-" + request.orderId());
        reservation.setOrderId(request.orderId());
        if (reservation.getStatus() == null || Boolean.TRUE.equals(reservation.getCompensated())) {
            reservation.setStatus("RESERVED");
            reservation.setCompensated(false);
            reservation.setCompensatedAt(null);
            reservation.setCompensationReason(null);
        }
        repository.save(reservation);
        return new ReservationResponse(reservation.getReservationId(), reservation.getOrderId(), reservation.getStatus());
    }

    public ReleaseResponse release(String reservationId) {
        InventoryReservation reservation = repository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found: " + reservationId));
        if (!Boolean.TRUE.equals(reservation.getCompensated())) {
            reservation.setStatus("RELEASED");
            reservation.setCompensated(true);
            reservation.setCompensatedAt(LocalDateTime.now());
            reservation.setCompensationReason("BUSINESS_PROCESS_COMPENSATION");
        }
        repository.save(reservation);
        return new ReleaseResponse(reservation.getReservationId(), reservation.getStatus());
    }
}
