package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.AddressType;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
    List<OrderAddress> findByOrderOrderIdOrderById(UUID orderId);

    Optional<OrderAddress> findByOrderOrderIdAndAddressType(UUID orderId, AddressType type);
}
