package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_contact_details")
public class CustomerContactDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 30)
    private ContactType contactType;
    @Column(name = "contact_value", nullable = false, length = 320)
    private String contactValue;
    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact;
    @Column(nullable = false)
    private boolean verified;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CustomerContactDetails() {
    }

    public CustomerContactDetails(Customer c, ContactType t, String v, boolean p, boolean verified) {
        customer = c;
        update(t, v, p, verified);
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

    public ContactType getContactType() {
        return contactType;
    }

    public String getContactValue() {
        return contactValue;
    }

    public boolean isPrimaryContact() {
        return primaryContact;
    }

    public boolean isVerified() {
        return verified;
    }

    public void update(ContactType t, String v, boolean p, boolean verified) {
        contactType = t;
        contactValue = v;
        primaryContact = p;
        this.verified = verified;
    }

    public enum ContactType {EMAIL, MOBILE, HOME_PHONE, WORK_PHONE}
}
