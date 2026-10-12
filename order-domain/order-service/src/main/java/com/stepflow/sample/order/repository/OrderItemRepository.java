package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderOrderIdOrderById(UUID orderId);

    Optional<OrderItem> findByOrderItemIdAndOrderOrderId(UUID itemId, UUID orderId);
}
