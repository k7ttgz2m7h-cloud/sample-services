package com.stepflow.sample.customer.service;

import com.stepflow.sample.customer.dto.CustomerDtos.*;
import com.stepflow.sample.customer.entity.Customer;
import com.stepflow.sample.customer.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer get(String customerId) {
        return repository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId));
    }

    public VerifyCustomerResponse verify(VerifyCustomerRequest request) {
        if (request.customerId() == null || request.customerId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId is required");
        }
        return new VerifyCustomerResponse(request.customerId(), "ACTIVE".equalsIgnoreCase(request.status()));
    }
}
