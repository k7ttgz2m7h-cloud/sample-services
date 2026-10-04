package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "customer_event", indexes = {
        @Index(name = "idx_customer_event_domain_created", columnList = "domain_id, created_at"),
        @Index(name = "idx_customer_event_correlation", columnList = "correlation_id"),
        @Index(name = "idx_customer_event_status_created", columnList = "event_status, created_at")
})
public class CustomerEvent {
    public static final String CUSTOMER_DOMAIN_TYPE = "CUSTOMER";
    public static final String CUSTOMER_TOPIC_NAME = "customer-trigger";
    public static final int CURRENT_EVENT_VERSION = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private UUID eventId;

    @Column(name = "domain_id", nullable = false, updatable = false)
    private UUID domainId;

    @Column(name = "domain_type", nullable = false, length = 60, updatable = false)
    private String domainType;

    @Column(name = "correlation_id", nullable = false, length = 160, updatable = false)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100, updatable = false)
    private EventType eventType;

    @Column(name = "event_version", nullable = false, updatable = false)
    private Integer eventVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false, length = 20)
    private EventStatus eventStatus;

    @Column(name = "topic_name", nullable = false, length = 255, updatable = false)
    private String topicName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_payload", nullable = false, columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> eventPayload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected CustomerEvent() {
    }

    public CustomerEvent(UUID domainId, String correlationId, EventType eventType,
                         Map<String, Object> eventPayload) {
        this.eventId = UUID.randomUUID();
        this.domainId = domainId;
        this.domainType = CUSTOMER_DOMAIN_TYPE;
        this.correlationId = correlationId;
        this.eventType = eventType;
        this.eventVersion = CURRENT_EVENT_VERSION;
        this.eventStatus = EventStatus.PENDING;
        this.topicName = CUSTOMER_TOPIC_NAME;
        this.eventPayload = Map.copyOf(eventPayload);
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getDomainId() {
        return domainId;
    }

    public String getDomainType() {
        return domainType;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Integer getEventVersion() {
        return eventVersion;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public String getTopicName() {
        return topicName;
    }

    public Map<String, Object> getEventPayload() {
        return eventPayload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void markProcessed() {
        eventStatus = EventStatus.PROCESSED;
        processedAt = LocalDateTime.now();
    }

    public void markFailed() {
        eventStatus = EventStatus.FAILED;
    }

    public enum EventType {
        CUSTOMER_CREATED,
        CUSTOMER_UPDATED,
        CUSTOMER_CONTACT_UPDATED,
        CUSTOMER_ADDRESS_UPDATED,
        CUSTOMER_PREFERENCE_UPDATED
    }

    public enum EventStatus { PENDING, PROCESSED, FAILED }
}
