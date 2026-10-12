package com.stepflow.sample.order.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.stepflow.sample.order.entity.OrderEnums.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false, unique = true, updatable = false)
    private UUID orderId;
    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus;
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;
    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotalAmount;
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount;
    @Column(name = "shipping_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal shippingAmount;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Order() {
    }

    public Order(UUID customerId, String currencyCode, BigDecimal shippingAmount) {
        this.orderId = UUID.randomUUID();
        this.customerId = customerId;
        this.currencyCode = currencyCode.toUpperCase();
        this.shippingAmount = shippingAmount;
        this.orderStatus = OrderStatus.CREATED;
        this.subtotalAmount = this.discountAmount = this.taxAmount = this.totalAmount = BigDecimal.ZERO;
    }

    @PrePersist
    void created() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void updated() {
        updatedAt = LocalDateTime.now();
    }

    public void update(String currencyCode, BigDecimal shippingAmount) {
        this.currencyCode = currencyCode.toUpperCase();
        this.shippingAmount = shippingAmount;
    }

    public void totals(BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total) {
        subtotalAmount = subtotal;
        discountAmount = discount;
        taxAmount = tax;
        totalAmount = total;
    }

    public void transition(OrderStatus status) {
        orderStatus = status;
    }

    public Long getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
