package com.stepflow.sample.pricing.controller;

import com.stepflow.sample.pricing.dto.PricingDtos.*;
import com.stepflow.sample.pricing.entity.PriceBook;
import com.stepflow.sample.pricing.service.PricingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService service;

    public PricingController(PricingService service) {
        this.service = service;
    }

    @GetMapping("/price-books/{priceBookId}")
    public PriceBook getPriceBook(@PathVariable String priceBookId) {
        return service.getPriceBook(priceBookId);
    }

    @PostMapping("/calculate")
    public CalculatePriceResponse calculate(@RequestBody CalculatePriceRequest request) {
        return service.calculate(request);
    }
}
