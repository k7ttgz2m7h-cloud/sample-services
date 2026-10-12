package com.stepflow.sample.order;

import com.stepflow.sample.order.dto.OrderRequests.*;
import com.stepflow.sample.order.dto.OrderResponses.OrderResponse;
import com.stepflow.sample.order.repository.*;
import com.stepflow.sample.order.service.OrderExceptions.*;
import com.stepflow.sample.order.service.OrderService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {
    @Autowired
    OrderService service;
    @Autowired
    OrderEventRepository events;
    @Autowired
    OrderAuditRepository audits;
    @Autowired
    Validator validator;

    @Test
    void createsRetrievesAndDeduplicatesOrder() {
        CreateOrderRequest request = createRequest();
        OrderResponse created = service.create(request, "test-key-1", "test");
        OrderResponse repeated = service.create(request, "test-key-1", "test");
        assertThat(created.orderId()).isNotNull().isEqualTo(repeated.orderId());
        assertThat(created.totalAmount()).isEqualByComparingTo("109.48");
        assertThat(service.get(created.orderId()).items()).hasSize(1);
        assertThat(events.count()).isPositive();
        assertThat(audits.count()).isPositive();
    }

    @Test
    void rejectsSameIdempotencyKeyForDifferentPayload() {
        service.create(createRequest(), "test-key-2", "test");
        CreateOrderRequest changed = new CreateOrderRequest(UUID.randomUUID(), "USD", createRequest().items(), createRequest().shippingAddress(), null, new BigDecimal("10.00"));
        assertThatThrownBy(() -> service.create(changed, "test-key-2", "test")).isInstanceOf(IdempotencyConflictException.class);
    }

    @Test
    void modifiesItemsAddressStatusAndCancels() {
        OrderResponse order = service.create(createRequest(), "test-key-3", "test");
        OrderResponse added = service.addItem(order.orderId(), new AddOrderItemRequest(null, "SKU-2", "Second", 1, new BigDecimal("10"), BigDecimal.ZERO, BigDecimal.ZERO), "test");
        UUID itemId = added.items().get(1).orderItemId();
        assertThat(added.totalAmount()).isEqualByComparingTo("119.48");
        service.updateItem(order.orderId(), itemId, new UpdateOrderItemRequest(null, "SKU-2", "Second", 2, new BigDecimal("10"), BigDecimal.ZERO, BigDecimal.ZERO), "test");
        service.removeItem(order.orderId(), itemId, "test");
        service.updateAddress(order.orderId(), AddressType.SHIPPING, new UpdateOrderAddressRequest(null, "New Street", null, "Austin", "TX", "73301", "US"), "test");
        service.updateStatus(order.orderId(), new UpdateOrderStatusRequest(OrderStatus.PENDING_PAYMENT, "Ready for payment"), "test");
        OrderResponse cancelled = service.cancel(order.orderId(), new CancelOrderRequest("Customer request"), "test");
        assertThat(cancelled.orderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void rejectsInvalidTransitionAndMissingOrder() {
        OrderResponse order = service.create(createRequest(), "test-key-4", "test");
        assertThatThrownBy(() -> service.updateStatus(order.orderId(), new UpdateOrderStatusRequest(OrderStatus.COMPLETED, "invalid"), "test")).isInstanceOf(InvalidOrderStatusTransitionException.class);
        assertThatThrownBy(() -> service.get(UUID.randomUUID())).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void validatesEmptyItemsInvalidQuantityAndNegativeAmount() {
        CreateOrderRequest empty = new CreateOrderRequest(UUID.randomUUID(), "USD", List.of(), address(), null, BigDecimal.ZERO);
        assertThat(validator.validate(empty)).isNotEmpty();
        CreateOrderItemRequest invalid = new CreateOrderItemRequest(null, "SKU", "Product", 0, BigDecimal.valueOf(-1), BigDecimal.ZERO, BigDecimal.ZERO);
        assertThat(validator.validate(invalid)).hasSizeGreaterThanOrEqualTo(2);
    }

    private CreateOrderRequest createRequest() {
        return new CreateOrderRequest(UUID.randomUUID(), "USD", List.of(new CreateOrderItemRequest(UUID.randomUUID(), "SKU-1001", "Sample Product", 2, new BigDecimal("49.99"), new BigDecimal("5.00"), new BigDecimal("4.50"))), address(), address(), new BigDecimal("10.00"));
    }

    private CreateOrderAddressRequest address() {
        return new CreateOrderAddressRequest(UUID.randomUUID(), "123 Main Street", null, "Dallas", "TX", "75001", "US");
    }
}
