package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerCommunicationPreference;
import com.stepflow.customer.domain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CustomerCommunicationPreferenceRepository extends JpaRepository<CustomerCommunicationPreference, Long> {
    List<CustomerCommunicationPreference> findByCustomerCustomerIdOrderById(UUID customerId);

    Optional<CustomerCommunicationPreference> findByCustomerCustomerIdAndCommunicationType(
            UUID customerId, CustomerCommunicationPreference.CommunicationType type);
}
