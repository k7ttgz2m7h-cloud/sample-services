package com.stepflow.sample.order.repository;

import com.stepflow.sample.order.entity.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAuditRepository extends JpaRepository<OrderAudit, Long> {
}
