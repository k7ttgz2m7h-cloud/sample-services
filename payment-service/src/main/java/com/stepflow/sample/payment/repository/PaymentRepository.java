package com.stepflow.sample.payment.repository;

import com.stepflow.sample.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByAuthorizationId(String authorizationId);
    Optional<Payment> findByCaptureId(String captureId);
}
