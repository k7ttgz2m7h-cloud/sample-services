package com.stepflow.sample.fulfillment.repository;

import com.stepflow.sample.fulfillment.entity.Fulfillment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, Long> {
    Optional<Fulfillment> findByFulfillmentId(String fulfillmentId);
    Optional<Fulfillment> findByOrderId(String orderId);
}
