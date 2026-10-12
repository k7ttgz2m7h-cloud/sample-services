package com.stepflow.sample.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.stepflow.sample.order.entity.OrderEnums.AddressType;

@Entity
@Table(name = "order_address", uniqueConstraints = @UniqueConstraint(name = "uk_order_address_type", columnNames = {"order_id", "address_type"}))
public class OrderAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_address_id", nullable = false, unique = true, updatable = false)
    private UUID orderAddressId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;
    @Column(name = "customer_address_id")
    private UUID customerAddressId;
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 20)
    private AddressType addressType;
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
    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected OrderAddress() {
    }

    public OrderAddress(Order order, AddressType type, UUID source, String line1, String line2, String city, String state, String postal, String country) {
        orderAddressId = UUID.randomUUID();
        this.order = order;
        addressType = type;
        update(source, line1, line2, city, state, postal, country);
    }

    public void update(UUID source, String line1, String line2, String city, String state, String postal, String country) {
        customerAddressId = source;
        addressLine1 = line1;
        addressLine2 = line2;
        this.city = city;
        this.state = state;
        postalCode = postal;
        countryCode = country.toUpperCase();
    }

    @PrePersist
    void created() {
        createdAt = LocalDateTime.now();
    }

    public UUID getOrderAddressId() {
        return orderAddressId;
    }

    public UUID getCustomerAddressId() {
        return customerAddressId;
    }

    public AddressType getAddressType() {
        return addressType;
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

    public String getCountryCode() {
        return countryCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
