package com.stepflow.customer.domain.service;

import com.stepflow.customer.domain.entity.CustomerEvent.EventType;
import com.stepflow.infrastructure.outbox.model.EventRecord;
import com.stepflow.infrastructure.outbox.model.OutboxEventRequest;
import com.stepflow.infrastructure.outbox.persistence.OutboxEventWriter;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/** Domain payload integration; persistence and publication belong to the shared library. */
@Service
public class CustomerEventService {
    private final OutboxEventWriter events;
    private final EntityManager entityManager;

    public CustomerEventService(OutboxEventWriter events, EntityManager entityManager) {
        this.events = events;
        this.entityManager = entityManager;
    }

    public EventRecord record(UUID domainId, String correlationId, EventType eventType,
                              Map<String, Object> eventPayload) {
        // JDBC inserts immediately: flush JPA changes so the domain foreign key exists.
        entityManager.flush();
        return events.record(new OutboxEventRequest(domainId, eventType.name(), eventPayload, correlationId));
    }
}
