package com.stepflow.sample.customer.controller;

import com.stepflow.sample.customer.dto.CustomerDtos.*;
import com.stepflow.sample.customer.entity.Customer;
import com.stepflow.sample.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/{customerId}")
    public Customer get(@PathVariable String customerId) {
        return service.get(customerId);
    }

    @PostMapping("/verify")
    public VerifyCustomerResponse verify(@RequestBody VerifyCustomerRequest request) {
        return service.verify(request);
    }
}
