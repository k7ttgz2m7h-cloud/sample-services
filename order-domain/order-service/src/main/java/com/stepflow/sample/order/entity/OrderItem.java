package com.stepflow.sample.order.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_item_id", nullable = false, unique = true, updatable = false)
    private UUID orderItemId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;
    @Column(name = "product_id")
    private UUID productId;
    @Column(nullable = false, length = 100)
    private String sku;
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected OrderItem() {
    }

    public OrderItem(Order order, UUID productId, String sku, String productName, int quantity, BigDecimal unitPrice, BigDecimal discountAmount, BigDecimal taxAmount) {
        this.orderItemId = UUID.randomUUID();
        this.order = order;
        update(productId, sku, productName, quantity, unitPrice, discountAmount, taxAmount);
    }

    public void update(UUID productId, String sku, String productName, int quantity, BigDecimal unitPrice, BigDecimal discountAmount, BigDecimal taxAmount) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discountAmount).add(taxAmount);
    }

    @PrePersist
    void created() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void updated() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
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
