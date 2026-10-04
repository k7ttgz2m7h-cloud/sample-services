package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface CustomerEventRepository extends JpaRepository<CustomerEvent, Long> {
    boolean existsByEventId(UUID eventId);

    List<CustomerEvent> findTop100ByEventStatusOrderByCreatedAtAsc(CustomerEvent.EventStatus eventStatus);
}
