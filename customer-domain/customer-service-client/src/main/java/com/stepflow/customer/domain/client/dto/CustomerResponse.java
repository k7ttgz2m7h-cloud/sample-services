package com.stepflow.customer.domain.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CustomerDetailsResponse details,
        List<CustomerContactResponse> contacts,
        List<CustomerDeliveryAddressResponse> deliveryAddresses,
        List<CustomerCommunicationPreferenceResponse> communicationPreferences) {

    public record CustomerDetailsResponse(
            Long id,
            LocalDate dateOfBirth,
            String gender,
            String preferredLanguage) {
    }

    public record CustomerContactResponse(
            Long id,
            String contactType,
            String contactValue,
            boolean primaryContact,
            boolean verified) {
    }

    public record AddressResponse(
            Long id,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country) {
    }

    public record CustomerDeliveryAddressResponse(
            Long id,
            String addressType,
            boolean primaryAddress,
            boolean active,
            String deliveryInstructions,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            AddressResponse address) {
    }

    public record CustomerCommunicationPreferenceResponse(
            Long id,
            String communicationType,
            boolean enabled,
            boolean preferred) {
    }
}
