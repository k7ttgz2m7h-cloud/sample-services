package com.stepflow.sample.shipping.repository;

import com.stepflow.sample.shipping.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByShipmentId(String shipmentId);
    Optional<Shipment> findByOrderId(String orderId);
}
