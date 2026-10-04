package com.stepflow.customer.domain.entity;

import jakarta.persistence.*;

import java.time.*;

@Entity
@Table(name = "customer_details", uniqueConstraints = @UniqueConstraint(name = "uk_customer_details_customer", columnNames = "customer_id"))
public class CustomerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(length = 40)
    private String gender;
    @Column(name = "preferred_language", length = 40)
    private String preferredLanguage;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CustomerDetails() {
    }

    public CustomerDetails(Customer c, LocalDate d, String g, String l) {
        customer = c;
        dateOfBirth = d;
        gender = g;
        preferredLanguage = l;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void update(LocalDate d, String g, String l) {
        dateOfBirth = d;
        gender = g;
        preferredLanguage = l;
    }
}
