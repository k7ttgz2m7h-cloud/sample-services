package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_communication_preference", uniqueConstraints = @UniqueConstraint(name = "uk_customer_communication_type", columnNames = {"customer_id", "communication_type"}))
public class CustomerCommunicationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false, length = 30)
    private CommunicationType communicationType;
    @Column(nullable = false)
    private boolean enabled;
    @Column(nullable = false)
    private boolean preferred;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CustomerCommunicationPreference() {
    }

    public CustomerCommunicationPreference(Customer c, CommunicationType t, boolean e, boolean p) {
        customer = c;
        communicationType = t;
        update(e, p);
    }

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public CommunicationType getCommunicationType() {
        return communicationType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void update(boolean e, boolean p) {
        enabled = e;
        preferred = p;
    }

    public enum CommunicationType {EMAIL, SMS, PHONE, MAIL}
}
