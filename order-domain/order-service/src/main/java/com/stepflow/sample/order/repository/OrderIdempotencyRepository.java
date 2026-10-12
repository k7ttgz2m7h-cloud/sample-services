package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface OrderIdempotencyRepository extends JpaRepository<OrderIdempotency, Long> {
    Optional<OrderIdempotency> findByIdempotencyKey(String key);
}
