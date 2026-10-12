package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
}
