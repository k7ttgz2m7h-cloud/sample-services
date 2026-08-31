package com.stepflow.sample.pricing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PriceBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String priceBookId;
    private String name;
    private String currency;

    public PriceBook() {}

    public PriceBook(Long id, String priceBookId, String name, String currency) {
        this.id = id;
        this.priceBookId = priceBookId;
        this.name = name;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public String getPriceBookId() { return priceBookId; }
    public String getName() { return name; }
    public String getCurrency() { return currency; }
}
