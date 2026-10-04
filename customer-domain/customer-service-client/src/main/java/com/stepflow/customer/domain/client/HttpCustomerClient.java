package com.stepflow.customer.domain.client;

import com.stepflow.customer.domain.client.dto.CustomerResponse;
import com.stepflow.customer.domain.client.dto.CustomerVerificationRequest;
import com.stepflow.customer.domain.client.dto.CustomerVerificationResponse;
import com.stepflow.customer.domain.client.dto.CustomerRequests.*;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerCommunicationPreferenceResponse;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerContactResponse;
import com.stepflow.customer.domain.client.dto.CustomerResponse.CustomerDeliveryAddressResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class HttpCustomerClient implements CustomerClient {
    private final RestClient restClient;

    public HttpCustomerClient(RestClient.Builder restClientBuilder, String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.post()
                .uri("/customers")
                .body(request)
                .retrieve()
                .body(CustomerResponse.class));
    }

    @Override
    public CustomerResponse getCustomer(UUID customerId) {
        Objects.requireNonNull(customerId, "customerId is required");
        return execute(() -> restClient.get()
                    .uri("/customers/{customerId}", customerId)
                    .retrieve()
                    .body(CustomerResponse.class));
    }

    @Override
    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.put()
                .uri("/customers/{customerId}", customerId)
                .body(request)
                .retrieve()
                .body(CustomerResponse.class));
    }

    @Override
    public CustomerVerificationResponse verifyCustomer(CustomerVerificationRequest request) {
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.post()
                    .uri("/customers/verify")
                    .body(request)
                    .retrieve()
                    .body(CustomerVerificationResponse.class));
    }

    @Override
    public CustomerContactResponse addContact(UUID customerId, CreateCustomerContactRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.post()
                .uri("/customers/{customerId}/contacts", customerId)
                .body(request)
                .retrieve()
                .body(CustomerContactResponse.class));
    }

    @Override
    public CustomerContactResponse updateContact(
            UUID customerId, Long contactId, UpdateCustomerContactRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(contactId, "contactId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.put()
                .uri("/customers/{customerId}/contacts/{contactId}", customerId, contactId)
                .body(request)
                .retrieve()
                .body(CustomerContactResponse.class));
    }

    @Override
    public CustomerDeliveryAddressResponse addDeliveryAddress(
            UUID customerId, CreateCustomerAddressRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.post()
                .uri("/customers/{customerId}/delivery-addresses", customerId)
                .body(request)
                .retrieve()
                .body(CustomerDeliveryAddressResponse.class));
    }

    @Override
    public CustomerDeliveryAddressResponse updateDeliveryAddress(
            UUID customerId, Long deliveryAddressId, UpdateCustomerAddressRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(deliveryAddressId, "deliveryAddressId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.put()
                .uri("/customers/{customerId}/delivery-addresses/{deliveryAddressId}",
                        customerId, deliveryAddressId)
                .body(request)
                .retrieve()
                .body(CustomerDeliveryAddressResponse.class));
    }

    @Override
    public void deactivateDeliveryAddress(UUID customerId, Long deliveryAddressId) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(deliveryAddressId, "deliveryAddressId is required");
        execute(() -> {
            restClient.delete()
                    .uri("/customers/{customerId}/delivery-addresses/{deliveryAddressId}",
                            customerId, deliveryAddressId)
                    .retrieve()
                    .toBodilessEntity();
            return null;
        });
    }

    @Override
    public List<CustomerCommunicationPreferenceResponse> updateCommunicationPreferences(
            UUID customerId, UpdateCommunicationPreferenceRequest request) {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(() -> restClient.put()
                .uri("/customers/{customerId}/communication-preferences", customerId)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {}));
    }

    private <T> T execute(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientResponseException exception) {
            throw CustomerClientException.from(exception);
        }
    }
}
