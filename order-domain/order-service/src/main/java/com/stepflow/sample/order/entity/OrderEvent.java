package com.stepflow.sample.order.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.*;

@Entity
@Table(name = "order_event")
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private UUID eventId;
    @Column(name = "domain_id", nullable = false, updatable = false)
    private UUID domainId;
    @Column(name = "domain_type", nullable = false, length = 60, updatable = false)
    private String domainType;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100, updatable = false)
    private OrderEventType eventType;
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
    @Column(name = "correlation_id", length = 160, updatable = false)
    private String correlationId;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    protected OrderEvent() {
    }

    public OrderEvent(Order order, OrderEventType type, String correlationId, Map<String, Object> payload) {
        eventId = UUID.randomUUID();
        domainId = order.getOrderId();
        domainType = "ORDER";
        eventType = type;
        eventVersion = 1;
        eventStatus = EventStatus.PENDING;
        topicName = "order.events";
        eventPayload = Map.copyOf(payload);
        this.correlationId = correlationId;
        createdAt = LocalDateTime.now();
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

    public OrderEventType getEventType() {
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

    public String getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
