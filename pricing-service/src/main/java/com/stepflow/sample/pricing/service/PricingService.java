package com.stepflow.sample.pricing.service;

import com.stepflow.sample.pricing.dto.PricingDtos.*;
import com.stepflow.sample.pricing.entity.PriceBook;
import com.stepflow.sample.pricing.repository.PriceBookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PricingService {
    private final PriceBookRepository repository;

    public PricingService(PriceBookRepository repository) {
        this.repository = repository;
    }

    public PriceBook getPriceBook(String priceBookId) {
        return repository.findByPriceBookId(priceBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Price book not found: " + priceBookId));
    }

    public CalculatePriceResponse calculate(CalculatePriceRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items are required");
        }
        int quantity = request.items() == null ? 0 : request.items().stream()
                .mapToInt(item -> item.quantity() == null ? 0 : item.quantity())
                .sum();
        return new CalculatePriceResponse(request.orderId(), BigDecimal.valueOf(quantity * 25L), "USD");
    }
}
