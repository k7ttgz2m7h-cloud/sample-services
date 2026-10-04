package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CustomerContactDetailsRepository extends JpaRepository<CustomerContactDetails, Long> {
    List<CustomerContactDetails> findByCustomerCustomerIdOrderById(UUID customerId);

    Optional<CustomerContactDetails> findByIdAndCustomerCustomerId(Long id, UUID customerId);
}
