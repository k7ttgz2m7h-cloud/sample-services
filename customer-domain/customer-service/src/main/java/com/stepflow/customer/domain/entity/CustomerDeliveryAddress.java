package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.*;

@Entity
@Table(name = "customer_delivery_address")
public class CustomerDeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 30)
    private AddressType addressType;
    @Column(name = "primary_address", nullable = false)
    private boolean primaryAddress;
    @Column(nullable = false)
    private boolean active;
    @Column(name = "delivery_instructions", length = 1000)
    private String deliveryInstructions;
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;
    @Column(name = "effective_to")
    private LocalDate effectiveTo;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CustomerDeliveryAddress() {
    }

    public CustomerDeliveryAddress(Customer c, Address a, AddressType t, boolean p, boolean active, String i, LocalDate from, LocalDate to) {
        customer = c;
        address = a;
        update(t, p, active, i, from, to);
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

    public Address getAddress() {
        return address;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(boolean p) {
        primaryAddress = p;
    }

    public boolean isActive() {
        return active;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void update(AddressType t, boolean p, boolean active, String i, LocalDate from, LocalDate to) {
        addressType = t;
        primaryAddress = p;
        this.active = active;
        deliveryInstructions = i;
        effectiveFrom = from;
        effectiveTo = to;
    }

    public void deactivate() {
        active = false;
        primaryAddress = false;
        effectiveTo = LocalDate.now();
    }

    public enum AddressType {HOME, WORK, SHIPPING, OTHER}
}
