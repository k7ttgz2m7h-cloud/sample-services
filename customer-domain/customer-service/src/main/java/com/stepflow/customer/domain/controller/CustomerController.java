package com.stepflow.customer.domain.controller;

import com.stepflow.customer.domain.dto.CustomerDtos.*;
import com.stepflow.customer.domain.dto.CustomerRequests.*;
import com.stepflow.customer.domain.dto.CustomerResponses.*;
import com.stepflow.customer.domain.service.CustomerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/customers", "/api/customers"})
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerAggregate> create(@RequestBody CreateCustomerRequest request,
                                                    @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request, changedBy));
    }

    @GetMapping("/{customerId}")
    public CustomerAggregate get(@PathVariable UUID customerId) {
        return customerService.get(customerId);
    }

    @PutMapping("/{customerId}")
    public CustomerAggregate update(@PathVariable UUID customerId, @RequestBody UpdateCustomerRequest request,
                                    @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return customerService.update(customerId, request, changedBy);
    }

    @PostMapping("/{customerId}/contacts")
    public ResponseEntity<Contact> addContact(@PathVariable UUID customerId,
                                              @RequestBody CreateCustomerContactRequest request,
                                              @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addContact(customerId, request, changedBy));
    }

    @PutMapping("/{customerId}/contacts/{contactId}")
    public Contact updateContact(@PathVariable UUID customerId, @PathVariable Long contactId,
                                 @RequestBody UpdateCustomerContactRequest request,
                                 @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return customerService.updateContact(customerId, contactId, request, changedBy);
    }

    @PostMapping("/{customerId}/delivery-addresses")
    public ResponseEntity<DeliveryAddress> addAddress(@PathVariable UUID customerId,
                                                      @RequestBody CreateCustomerAddressRequest request,
                                                      @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addDeliveryAddress(customerId, request, changedBy));
    }

    @PutMapping("/{customerId}/delivery-addresses/{deliveryAddressId}")
    public DeliveryAddress updateAddress(@PathVariable UUID customerId, @PathVariable Long deliveryAddressId,
                                         @RequestBody UpdateCustomerAddressRequest request,
                                         @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return customerService.updateDeliveryAddress(customerId, deliveryAddressId, request, changedBy);
    }

    @DeleteMapping("/{customerId}/delivery-addresses/{deliveryAddressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID customerId, @PathVariable Long deliveryAddressId,
                           @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        customerService.deactivateDeliveryAddress(customerId, deliveryAddressId, changedBy);
    }

    @PutMapping("/{customerId}/communication-preferences")
    public List<CommunicationPreference> updatePreferences(
            @PathVariable UUID customerId, @RequestBody UpdateCommunicationPreferenceRequest request,
            @RequestHeader(value = "X-Changed-By", defaultValue = "api") String changedBy) {
        return customerService.updatePreferences(customerId, request, changedBy);
    }

    @GetMapping("/{customerId}/audit")
    public List<Audit> audit(@PathVariable UUID customerId) {
        return customerService.audit(customerId);
    }

    @PostMapping("/verify")
    public VerifyCustomerResponse verify(@RequestBody VerifyCustomerRequest request) {
        return customerService.verify(request);
    }
}
