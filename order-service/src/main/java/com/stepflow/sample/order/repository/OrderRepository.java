package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderRecord, Long> {
    Optional<OrderRecord> findByOrderId(String orderId);
    Optional<OrderRecord> findByClientRequestId(String clientRequestId);
}
