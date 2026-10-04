package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerAuditRepository extends JpaRepository<CustomerAudit, Long> {
    List<CustomerAudit> findByCustomerCustomerIdOrderByChangedAtDescIdDesc(UUID customerId);
}
