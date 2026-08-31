package com.stepflow.sample.inventory.repository;

import com.stepflow.sample.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    Optional<InventoryReservation> findByReservationId(String reservationId);
    Optional<InventoryReservation> findByOrderId(String orderId);
}
