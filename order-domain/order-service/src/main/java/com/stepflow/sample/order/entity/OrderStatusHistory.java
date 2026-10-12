package com.stepflow.sample.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.stepflow.sample.order.entity.OrderEnums.OrderStatus;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private OrderStatus previousStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private OrderStatus newStatus;
    @Column(name = "change_reason", length = 500)
    private String changeReason;
    @Column(name = "changed_by", nullable = false, length = 160)
    private String changedBy;
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected OrderStatusHistory() {
    }

    public OrderStatusHistory(Order order, OrderStatus previous, OrderStatus next, String reason, String changedBy) {
        this.order = order;
        previousStatus = previous;
        newStatus = next;
        changeReason = reason;
        this.changedBy = changedBy;
        changedAt = LocalDateTime.now();
    }
}
