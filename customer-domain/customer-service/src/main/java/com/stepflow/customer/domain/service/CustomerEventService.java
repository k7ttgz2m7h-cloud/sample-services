package com.stepflow.customer.domain.service;

import com.stepflow.customer.domain.entity.CustomerEvent;
import com.stepflow.customer.domain.entity.CustomerEvent.EventType;
import com.stepflow.customer.domain.repository.CustomerEventRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@Service
public class CustomerEventService {
    private final CustomerEventRepository customerEventRepository;

    public CustomerEventService(CustomerEventRepository customerEventRepository) {
        this.customerEventRepository = customerEventRepository;
    }

    public CustomerEvent record(UUID domainId, String correlationId, EventType eventType,
                                Map<String, Object> eventPayload) {
        return customerEventRepository.save(new CustomerEvent(domainId, correlationId, eventType, eventPayload));
    }

    public List<CustomerEvent> findPendingEvents() {
        return customerEventRepository.findTop100ByEventStatusOrderByCreatedAtAsc(CustomerEvent.EventStatus.PENDING);
    }

    public void markProcessed(CustomerEvent customerEvent) {
        customerEvent.markProcessed();
        customerEventRepository.save(customerEvent);
    }

    public void markFailed(CustomerEvent customerEvent) {
        customerEvent.markFailed();
        customerEventRepository.save(customerEvent);
    }
}
