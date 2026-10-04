package com.stepflow.customer.domain.client.dto;

import java.time.LocalDate;
import java.util.List;

public final class CustomerRequests {
    private CustomerRequests() {
    }

    public record CustomerDetailsRequest(
            LocalDate dateOfBirth,
            String gender,
            String preferredLanguage) {
    }

    public record CreateCustomerContactRequest(
            String contactType,
            String contactValue,
            boolean primaryContact,
            boolean verified) {
    }

    public record UpdateCustomerContactRequest(
            String contactType,
            String contactValue,
            boolean primaryContact,
            boolean verified) {
    }

    public record AddressRequest(
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country) {
    }

    public record CreateCustomerAddressRequest(
            AddressRequest address,
            String addressType,
            boolean primaryAddress,
            boolean active,
            String deliveryInstructions,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
    }

    public record UpdateCustomerAddressRequest(
            AddressRequest address,
            String addressType,
            boolean primaryAddress,
            boolean active,
            String deliveryInstructions,
            LocalDate effectiveFrom,
            LocalDate effectiveTo) {
    }

    public record CommunicationPreferenceRequest(
            String communicationType,
            boolean enabled,
            boolean preferred) {
    }

    public record CreateCustomerRequest(
            String firstName,
            String lastName,
            String status,
            CustomerDetailsRequest customerDetails,
            List<CreateCustomerContactRequest> contactDetails,
            List<CreateCustomerAddressRequest> deliveryAddresses,
            List<CommunicationPreferenceRequest> communicationPreferences) {
    }

    public record UpdateCustomerRequest(
            String firstName,
            String lastName,
            String status,
            CustomerDetailsRequest customerDetails) {
    }

    public record UpdateCommunicationPreferenceRequest(
            List<CommunicationPreferenceRequest> communicationPreferences) {
    }
}
