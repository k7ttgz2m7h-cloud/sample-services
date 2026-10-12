package com.stepflow.sample.order.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.IdempotencyRequestStatus;

@Entity
@Table(name = "order_idempotency")
public class OrderIdempotency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 160, updatable = false)
    private String idempotencyKey;
    @Column(name = "request_hash", nullable = false, length = 128, updatable = false)
    private String requestHash;
    @Column(name = "order_id")
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    private IdempotencyRequestStatus requestStatus;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "jsonb")
    private Map<String, Object> responsePayload;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected OrderIdempotency() {
    }

    public OrderIdempotency(String key, String hash) {
        idempotencyKey = key;
        requestHash = hash;
        requestStatus = IdempotencyRequestStatus.PROCESSING;
        createdAt = LocalDateTime.now();
    }

    public void complete(UUID orderId, Map<String, Object> response) {
        this.orderId = orderId;
        responsePayload = Map.copyOf(response);
        requestStatus = IdempotencyRequestStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }

    public String getRequestHash() {
        return requestHash;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public IdempotencyRequestStatus getRequestStatus() {
        return requestStatus;
    }
}
