package com.stepflow.sample.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private Integer availableQuantity;

    public Inventory() {}

    public Inventory(Long id, String productId, Integer availableQuantity) {
        this.id = id;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }
}
