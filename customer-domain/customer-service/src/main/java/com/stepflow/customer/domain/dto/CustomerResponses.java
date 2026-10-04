package com.stepflow.customer.domain.dto;

import com.stepflow.customer.domain.entity.CustomerAudit;
import com.stepflow.customer.domain.entity.CustomerCommunicationPreference;
import com.stepflow.customer.domain.entity.CustomerContactDetails;
import com.stepflow.customer.domain.entity.CustomerDeliveryAddress;

import java.time.*;
import java.util.List;
import java.util.UUID;

public final class CustomerResponses {
    private CustomerResponses() {
    }

    public record Details(Long id, LocalDate dateOfBirth, String gender, String preferredLanguage) {
    }

    public record Contact(Long id, CustomerContactDetails.ContactType contactType, String contactValue,
                          boolean primaryContact, boolean verified) {
    }

    public record Address(Long id, String addressLine1, String addressLine2, String city, String state,
                          String postalCode, String country) {
    }

    public record DeliveryAddress(Long id, CustomerDeliveryAddress.AddressType addressType, boolean primaryAddress,
                                  boolean active, String deliveryInstructions, LocalDate effectiveFrom,
                                  LocalDate effectiveTo, Address address) {
    }

    public record CommunicationPreference(Long id, CustomerCommunicationPreference.CommunicationType communicationType,
                                          boolean enabled, boolean preferred) {
    }

    public record CustomerAggregate(UUID customerId, String firstName, String lastName, String status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt, Details details,
                                    List<Contact> contacts, List<DeliveryAddress> deliveryAddresses,
                                    List<CommunicationPreference> communicationPreferences) {
    }

    public record Audit(Long id, CustomerAudit.Action action, String entityType, String changedField, String oldValue,
                        String newValue, String changedBy, LocalDateTime changedAt) {
    }
}
