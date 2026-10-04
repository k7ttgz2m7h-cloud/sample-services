package com.stepflow.customer.domain.client;

import com.stepflow.customer.domain.client.dto.CustomerResponse;
import com.stepflow.customer.domain.client.dto.CustomerVerificationRequest;
import com.stepflow.customer.domain.client.dto.CustomerVerificationResponse;
import com.stepflow.customer.domain.client.dto.CustomerRequests.*;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerContactResponse;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerDeliveryAddressResponse;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerCommunicationPreferenceResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerClient {
    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomer(UUID customerId);

    CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request);

    CustomerVerificationResponse verifyCustomer(CustomerVerificationRequest request);

    CustomerContactResponse addContact(UUID customerId, CreateCustomerContactRequest request);

    CustomerContactResponse updateContact(
            UUID customerId,
            Long contactId,
            UpdateCustomerContactRequest request);

    CustomerDeliveryAddressResponse addDeliveryAddress(
            UUID customerId,
            CreateCustomerAddressRequest request);

    CustomerDeliveryAddressResponse updateDeliveryAddress(
            UUID customerId,
            Long deliveryAddressId,
            UpdateCustomerAddressRequest request);

    void deactivateDeliveryAddress(UUID customerId, Long deliveryAddressId);

    List<CustomerCommunicationPreferenceResponse> updateCommunicationPreferences(
            UUID customerId,
            UpdateCommunicationPreferenceRequest request);
}
