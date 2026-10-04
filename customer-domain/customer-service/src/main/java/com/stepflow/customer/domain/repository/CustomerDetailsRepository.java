package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {
    Optional<CustomerDetails> findByCustomerCustomerId(UUID customerId);
}
