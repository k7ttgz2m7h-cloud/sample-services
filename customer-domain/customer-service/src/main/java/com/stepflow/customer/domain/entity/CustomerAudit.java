package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_audit")
public class CustomerAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private Action action;
    @Column(name = "entity_type", nullable = false, length = 80)
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

    protected CustomerAudit() {
    }

    public CustomerAudit(Customer c, Action a, String type, String field, String old, String value, String by, String correlation) {
        customer = c;
        action = a;
        entityType = type;
        changedField = field;
        oldValue = old;
        newValue = value;
        changedBy = by;
        correlationId = correlation;
        changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Action getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getChangedField() {
        return changedField;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public enum Action {CUSTOMER_CREATED, CUSTOMER_UPDATED, CONTACT_ADDED, CONTACT_UPDATED, ADDRESS_ADDED, ADDRESS_UPDATED, DELIVERY_ADDRESS_ADDED, DELIVERY_ADDRESS_UPDATED, DELIVERY_ADDRESS_DEACTIVATED, PRIMARY_DELIVERY_ADDRESS_CHANGED, COMMUNICATION_PREFERENCE_UPDATED, CUSTOMER_STATUS_CHANGED}
}
