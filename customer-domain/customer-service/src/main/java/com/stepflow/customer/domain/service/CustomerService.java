package com.stepflow.customer.domain.service;

import com.stepflow.customer.domain.entity.*;
import com.stepflow.customer.domain.dto.CustomerDtos.*;
import com.stepflow.customer.domain.dto.CustomerRequests.*;
import com.stepflow.customer.domain.dto.CustomerResponses;
import com.stepflow.customer.domain.repository.*;
import com.stepflow.customer.domain.entity.*;
import com.stepflow.customer.domain.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.stepflow.customer.domain.entity.CustomerAudit.Action.*;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final CustomerContactDetailsRepository customerContactDetailsRepository;
    private final AddressRepository addressRepository;
    private final CustomerDeliveryAddressRepository customerDeliveryAddressRepository;
    private final CustomerCommunicationPreferenceRepository customerCommunicationPreferenceRepository;
    private final CustomerAuditRepository customerAuditRepository;
    private final CustomerEventService customerEventService;

    public CustomerService(CustomerRepository customerRepository, CustomerDetailsRepository customerDetailsRepository,
                           CustomerContactDetailsRepository customerContactDetailsRepository, AddressRepository addressRepository,
                           CustomerDeliveryAddressRepository customerDeliveryAddressRepository, CustomerCommunicationPreferenceRepository customerCommunicationPreferenceRepository,
                           CustomerAuditRepository customerAuditRepository, CustomerEventService customerEventService) {
        this.customerRepository = customerRepository;
        this.customerDetailsRepository = customerDetailsRepository;
        this.customerContactDetailsRepository = customerContactDetailsRepository;
        this.addressRepository = addressRepository;
        this.customerDeliveryAddressRepository = customerDeliveryAddressRepository;
        this.customerCommunicationPreferenceRepository = customerCommunicationPreferenceRepository;
        this.customerAuditRepository = customerAuditRepository;
        this.customerEventService = customerEventService;
    }

    private static <T> List<T> list(List<T> x) {
        return x == null ? List.of() : x;
    }

    private static String status(String x) {
        return x == null || x.isBlank() ? "ACTIVE" : x.toUpperCase();
    }

    private static void require(String x, String field) {
        if (x == null || x.isBlank()) throw bad(field + " is required");
    }

    private static ResponseStatusException bad(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private static ResponseStatusException conflict(String m) {
        return new ResponseStatusException(HttpStatus.CONFLICT, m);
    }

    private static ResponseStatusException notFound(String type, Object id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " not found: " + id);
    }

    @Transactional
    public CustomerResponses.CustomerAggregate create(CreateCustomerRequest request, String changedBy) {
        require(request.firstName(), "firstName");
        require(request.lastName(), "lastName");
        String correlationId = newCorrelationId();
        Customer customer = customerRepository.save(new Customer(request.firstName(), request.lastName(),
                status(request.status())));
        if (request.customerDetails() != null)
            customerDetailsRepository.save(toDetails(customer, request.customerDetails()));
        for (CreateCustomerContactRequest contactDetails : list(request.contactDetails()))
            customerContactDetailsRepository.save(toContact(customer, contactDetails));
        for (CreateCustomerAddressRequest deliveryAddress : list(request.deliveryAddresses()))
            addDelivery(customer, deliveryAddress, correlationId, changedBy, false);
        for (CommunicationPreferenceRequest communicationPreference : list(request.communicationPreferences()))
            customerCommunicationPreferenceRepository.save(toPreference(customer, communicationPreference));
        audit(customer, CUSTOMER_CREATED, "Customer", null, null, customer.getStatus(), changedBy, correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_CREATED,
                Map.of("status", customer.getStatus()));
        return aggregate(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponses.CustomerAggregate get(UUID customerId) {
        return aggregate(customer(customerId));
    }

    @Transactional
    public CustomerResponses.CustomerAggregate update(UUID customerId, UpdateCustomerRequest request, String changedBy) {
        Customer customer = customer(customerId);
        String previousStatus = customer.getStatus();
        String correlationId = newCorrelationId();
        require(request.firstName(), "firstName");
        require(request.lastName(), "lastName");
        customer.update(request.firstName(), request.lastName(), status(request.status()));
        if (request.customerDetails() != null) {
            CustomerDetails customerDetails = customerDetailsRepository.findByCustomerCustomerId(customer.getCustomerId())
                    .orElseGet(() -> new CustomerDetails(customer, null, null, null));
            customerDetails.update(request.customerDetails().dateOfBirth(), request.customerDetails().gender(),
                    request.customerDetails().preferredLanguage());
            customerDetailsRepository.save(customerDetails);
        }
        audit(customer, CUSTOMER_UPDATED, "Customer", null, null, null, changedBy, correlationId);
        if (!Objects.equals(previousStatus, customer.getStatus()))
            audit(customer, CUSTOMER_STATUS_CHANGED, "Customer", "status", previousStatus, customer.getStatus(),
                    changedBy, correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_UPDATED,
                Map.of("status", customer.getStatus()));
        return aggregate(customer);
    }

    @Transactional
    public CustomerResponses.Contact addContact(UUID customerId, CreateCustomerContactRequest request,
                                                String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        validate(request);
        CustomerContactDetails saved = customerContactDetailsRepository.save(toContact(customer, request));
        audit(customer, CONTACT_ADDED, "CustomerContactDetails", null, null, saved.getContactValue(), changedBy,
                correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_CONTACT_UPDATED,
                Map.of("contactId", saved.getId(), "operation", "CREATED"));
        return contact(saved);
    }

    @Transactional
    public CustomerResponses.Contact updateContact(UUID customerId, Long contactId,
                                                   UpdateCustomerContactRequest request, String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        validate(request);
        CustomerContactDetails contactDetails = customerContactDetailsRepository
                .findByIdAndCustomerCustomerId(contactId, customer.getCustomerId())
                .orElseThrow(() -> notFound("Contact", contactId));
        String previousContactValue = contactDetails.getContactValue();
        contactDetails.update(request.contactType(), request.contactValue(), request.primaryContact(), request.verified());
        audit(customer, CONTACT_UPDATED, "CustomerContactDetails", "contactValue", previousContactValue,
                contactDetails.getContactValue(), changedBy, correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_CONTACT_UPDATED,
                Map.of("contactId", contactId, "operation", "UPDATED"));
        return contact(contactDetails);
    }

    @Transactional
    public CustomerResponses.DeliveryAddress addDeliveryAddress(UUID customerId, CreateCustomerAddressRequest request,
                                                                String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        CustomerDeliveryAddress deliveryAddress = addDelivery(customer, request, correlationId, changedBy, true);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_ADDRESS_UPDATED,
                Map.of("deliveryAddressId", deliveryAddress.getId(), "operation", "CREATED"));
        return delivery(deliveryAddress);
    }

    @Transactional
    public CustomerResponses.DeliveryAddress updateDeliveryAddress(UUID customerId, Long deliveryAddressId,
                                                                   UpdateCustomerAddressRequest request,
                                                                   String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        validate(request);
        CustomerDeliveryAddress deliveryAddress = delivery(customer, deliveryAddressId);
        Address address = deliveryAddress.getAddress();
        address.update(request.address().addressLine1(), request.address().addressLine2(), request.address().city(),
                request.address().state(), request.address().postalCode(), request.address().country().toUpperCase());
        if (request.primaryAddress()) unsetPrimary(customer.getCustomerId(), deliveryAddress.getId());
        deliveryAddress.update(request.addressType(), request.primaryAddress(), request.active(),
                request.deliveryInstructions(), request.effectiveFrom(), request.effectiveTo());
        audit(customer, ADDRESS_UPDATED, "Address", null, null, null, changedBy, correlationId);
        audit(customer, DELIVERY_ADDRESS_UPDATED, "CustomerDeliveryAddress", null, null, null, changedBy,
                correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_ADDRESS_UPDATED,
                Map.of("deliveryAddressId", deliveryAddressId, "operation", "UPDATED"));
        return delivery(deliveryAddress);
    }

    @Transactional
    public void deactivateDeliveryAddress(UUID customerId, Long deliveryAddressId, String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        CustomerDeliveryAddress deliveryAddress = delivery(customer, deliveryAddressId);
        deliveryAddress.deactivate();
        audit(customer, DELIVERY_ADDRESS_DEACTIVATED, "CustomerDeliveryAddress", "active", "true", "false",
                changedBy, correlationId);
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_ADDRESS_UPDATED,
                Map.of("deliveryAddressId", deliveryAddressId, "operation", "DEACTIVATED"));
    }

    @Transactional
    public List<CustomerResponses.CommunicationPreference> updatePreferences(
            UUID customerId, UpdateCommunicationPreferenceRequest request, String changedBy) {
        Customer customer = customer(customerId);
        String correlationId = newCorrelationId();
        if (request == null || request.communicationPreferences() == null)
            throw bad("communicationPreferences is required");
        for (CommunicationPreferenceRequest communicationPreference : request.communicationPreferences()) {
            if (communicationPreference.communicationType() == null) throw bad("communicationType is required");
            CustomerCommunicationPreference preference = customerCommunicationPreferenceRepository
                    .findByCustomerCustomerIdAndCommunicationType(customer.getCustomerId(),
                            communicationPreference.communicationType())
                    .orElseGet(() -> new CustomerCommunicationPreference(customer,
                            communicationPreference.communicationType(), communicationPreference.enabled(),
                            communicationPreference.preferred()));
            preference.update(communicationPreference.enabled(), communicationPreference.preferred());
            customerCommunicationPreferenceRepository.save(preference);
            audit(customer, COMMUNICATION_PREFERENCE_UPDATED, "CustomerCommunicationPreference", "enabled", null,
                    String.valueOf(communicationPreference.enabled()), changedBy, correlationId);
        }
        recordEvent(customer, correlationId, CustomerEvent.EventType.CUSTOMER_PREFERENCE_UPDATED,
                Map.of("preferenceCount", request.communicationPreferences().size()));
        return customerCommunicationPreferenceRepository.findByCustomerCustomerIdOrderById(customer.getCustomerId()).stream()
                .map(this::preference).toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerResponses.Audit> audit(UUID customerId) {
        Customer customer = customer(customerId);
        return customerAuditRepository.findByCustomerCustomerIdOrderByChangedAtDescIdDesc(customer.getCustomerId())
                .stream().map(this::auditResponse).toList();
    }

    @Transactional(readOnly = true)
    public VerifyCustomerResponse verify(VerifyCustomerRequest verifyCustomerRequest) {
        require(String.valueOf(verifyCustomerRequest.customerId()), "customerId");
        Customer c = customer(verifyCustomerRequest.customerId());
        return new VerifyCustomerResponse(c.getCustomerId(), "ACTIVE".equalsIgnoreCase(c.getStatus()));
    }

    private CustomerDeliveryAddress addDelivery(Customer customer, CreateCustomerAddressRequest request,
                                                String correlationId, String changedBy, boolean writeAudit) {
        validate(request);
        if (request.primaryAddress()) unsetPrimary(customer.getCustomerId(), null);
        Address address = addressRepository.save(toAddress(request.address()));
        CustomerDeliveryAddress deliveryAddress = customerDeliveryAddressRepository.save(new CustomerDeliveryAddress(
                customer, address, request.addressType(), request.primaryAddress(), request.active(),
                request.deliveryInstructions(), request.effectiveFrom(), request.effectiveTo()));
        if (writeAudit) {
            audit(customer, ADDRESS_ADDED, "Address", null, null, String.valueOf(address.getId()), changedBy,
                    correlationId);
            audit(customer, DELIVERY_ADDRESS_ADDED, "CustomerDeliveryAddress", null, null,
                    String.valueOf(deliveryAddress.getId()), changedBy, correlationId);
        }
        return deliveryAddress;
    }

    private void unsetPrimary(UUID customerId, Long except) {
        if (except == null) customerDeliveryAddressRepository.clearPrimary(customerId);
        else customerDeliveryAddressRepository.clearPrimaryExcept(customerId, except);
    }

    private CustomerDeliveryAddress delivery(Customer c, Long id) {
        return customerDeliveryAddressRepository.findByIdAndCustomerCustomerId(id, c.getCustomerId())
                .orElseThrow(() -> notFound("Delivery address", id));
    }

    private Customer customer(UUID customerId) {
        return customerRepository.findByCustomerId(customerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + customerId));
    }

    private CustomerResponses.CustomerAggregate aggregate(Customer customer) {
        CustomerResponses.Details d = customerDetailsRepository.findByCustomerCustomerId(customer.getCustomerId())
                .map(x -> new CustomerResponses.Details(x.getId(), x.getDateOfBirth(), x.getGender(), x.getPreferredLanguage())).orElse(null);
        return new CustomerResponses.CustomerAggregate(customer.getCustomerId(), customer.getFirstName(), customer.getLastName(), customer.getStatus(), customer.getCreatedAt(), customer.getUpdatedAt(), d, customerContactDetailsRepository.findByCustomerCustomerIdOrderById(customer.getCustomerId()).stream().map(this::contact).toList(), customerDeliveryAddressRepository.findAggregateAddresses(customer.getCustomerId()).stream().map(this::delivery).toList(), customerCommunicationPreferenceRepository.findByCustomerCustomerIdOrderById(customer.getCustomerId()).stream().map(this::preference).toList());
    }

    private CustomerResponses.Contact contact(CustomerContactDetails customerContactDetails) {
        return new CustomerResponses.Contact(customerContactDetails.getId(), customerContactDetails.getContactType(), customerContactDetails.getContactValue(), customerContactDetails.isPrimaryContact(), customerContactDetails.isVerified());
    }

    private CustomerResponses.DeliveryAddress delivery(CustomerDeliveryAddress customerDeliveryAddress) {
        Address a = customerDeliveryAddress.getAddress();
        return new CustomerResponses.DeliveryAddress(customerDeliveryAddress.getId(), customerDeliveryAddress.getAddressType(), customerDeliveryAddress.isPrimaryAddress(), customerDeliveryAddress.isActive(), customerDeliveryAddress.getDeliveryInstructions(), customerDeliveryAddress.getEffectiveFrom(), customerDeliveryAddress.getEffectiveTo(), new CustomerResponses.Address(a.getId(), a.getAddressLine1(), a.getAddressLine2(), a.getCity(), a.getState(), a.getPostalCode(), a.getCountry()));
    }

    private CustomerResponses.CommunicationPreference preference(CustomerCommunicationPreference customerCommunicationPreference) {
        return new CustomerResponses.CommunicationPreference(customerCommunicationPreference.getId(), customerCommunicationPreference.getCommunicationType(), customerCommunicationPreference.isEnabled(), customerCommunicationPreference.isPreferred());
    }

    private CustomerResponses.Audit auditResponse(CustomerAudit customerAudit) {
        return new CustomerResponses.Audit(customerAudit.getId(), customerAudit.getAction(),
                customerAudit.getEntityType(), customerAudit.getChangedField(), customerAudit.getOldValue(),
                customerAudit.getNewValue(), customerAudit.getChangedBy(), customerAudit.getChangedAt());
    }

    private CustomerDetails toDetails(Customer customer, CustomerDetailsRequest request) {
        return new CustomerDetails(customer, request.dateOfBirth(), request.gender(), request.preferredLanguage());
    }

    private CustomerContactDetails toContact(Customer customer, CreateCustomerContactRequest request) {
        validate(request);
        return new CustomerContactDetails(customer, request.contactType(), request.contactValue(),
                request.primaryContact(), request.verified());
    }

    private Address toAddress(AddressRequest address) {
        require(address.addressLine1(), "addressLine1");
        require(address.city(), "city");
        require(address.state(), "state");
        require(address.postalCode(), "postalCode");
        require(address.country(), "country");
        if (address.country().length() != 2) throw bad("country must be a 2-letter code");
        return new Address(address.addressLine1(), address.addressLine2(), address.city(), address.state(), address.postalCode(), address.country().toUpperCase());
    }

    private CustomerCommunicationPreference toPreference(Customer customer, CommunicationPreferenceRequest request) {
        if (request.communicationType() == null) throw bad("communicationType is required");
        return new CustomerCommunicationPreference(customer, request.communicationType(), request.enabled(),
                request.preferred());
    }

    private void validate(CreateCustomerContactRequest request) {
        if (request == null || request.contactType() == null) throw bad("contactType is required");
        require(request.contactValue(), "contactValue");
    }

    private void validate(UpdateCustomerContactRequest request) {
        if (request == null || request.contactType() == null) throw bad("contactType is required");
        require(request.contactValue(), "contactValue");
    }

    private void validate(CreateCustomerAddressRequest request) {
        if (request == null) throw bad("request body is required");
        validateAddress(request.address(), request.addressType(), request.primaryAddress(), request.active());
    }

    private void validate(UpdateCustomerAddressRequest request) {
        if (request == null) throw bad("request body is required");
        validateAddress(request.address(), request.addressType(), request.primaryAddress(), request.active());
    }

    private void validateAddress(AddressRequest address, CustomerDeliveryAddress.AddressType addressType,
                                 boolean primaryAddress, boolean active) {
        if (address == null) throw bad("address is required");
        if (addressType == null) throw bad("addressType is required");
        if (primaryAddress && !active)
            throw bad("Primary delivery address must be active");
        toAddress(address);
    }

    private void audit(Customer c, CustomerAudit.Action action, String type, String field, String old, String value, String actor, String correlation) {
        customerAuditRepository.save(new CustomerAudit(c, action, type, field, old, value, actor, correlation));
    }

    private void recordEvent(Customer customer, String correlationId, CustomerEvent.EventType eventType,
                             Map<String, Object> eventPayload) {
        customerEventService.record(customer.getCustomerId(), correlationId, eventType, eventPayload);
    }

    private String newCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
