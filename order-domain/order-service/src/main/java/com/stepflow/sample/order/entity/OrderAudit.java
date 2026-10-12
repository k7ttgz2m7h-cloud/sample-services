package com.stepflow.sample.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.stepflow.sample.order.entity.OrderEnums.AuditAction;

@Entity
@Table(name = "order_audit")
public class OrderAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private AuditAction action;
    @Column(name = "entity_type", nullable = false, length = 60)
    private String entityType;
    @Column(name = "changed_field", length = 120)
    private String changedField;
    @Column(name = "old_value", length = 2000)
    private String oldValue;
    @Column(name = "new_value", length = 2000)
    private String newValue;
    @Column(name = "changed_by", nullable = false, length = 160)
    private String changedBy;
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
    @Column(name = "correlation_id", nullable = false, length = 160)
    private String correlationId;

    protected OrderAudit() {
    }

    public OrderAudit(Order order, AuditAction action, String entityType, String field, String oldValue, String newValue, String changedBy, String correlationId) {
        this.order = order;
        this.action = action;
        this.entityType = entityType;
        changedField = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.correlationId = correlationId;
        changedAt = LocalDateTime.now();
    }
}
