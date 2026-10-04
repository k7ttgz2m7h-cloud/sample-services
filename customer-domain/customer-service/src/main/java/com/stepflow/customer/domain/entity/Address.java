package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "address_line1", nullable = false, length = 200)
    private String addressLine1;
    @Column(name = "address_line2", length = 200)
    private String addressLine2;
    @Column(nullable = false, length = 100)
    private String city;
    @Column(nullable = false, length = 100)
    private String state;
    @Column(name = "postal_code", nullable = false, length = 30)
    private String postalCode;
    @Column(nullable = false, length = 2)
    private String country;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Address() {
    }

    public Address(String l1, String l2, String c, String s, String p, String country) {
        update(l1, l2, c, s, p, country);
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

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void update(String l1, String l2, String c, String s, String p, String country) {
        addressLine1 = l1;
        addressLine2 = l2;
        city = c;
        state = s;
        postalCode = p;
        this.country = country;
    }
}
