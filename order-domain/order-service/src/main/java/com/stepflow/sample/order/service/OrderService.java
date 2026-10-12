package com.stepflow.sample.order.service;

import com.stepflow.sample.order.dto.OrderRequests.*;
import com.stepflow.sample.order.dto.OrderResponses.*;
import com.stepflow.sample.order.entity.*;
import com.stepflow.sample.order.repository.*;
import org.springframework.stereotype.Service;
import com.stepflow.infrastructure.outbox.persistence.OutboxEventWriter;
import com.stepflow.infrastructure.outbox.model.OutboxEventRequest;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import static com.stepflow.sample.order.entity.OrderEnums.*;
import static com.stepflow.sample.order.service.OrderExceptions.*;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final OrderAddressRepository addresses;
    private final OrderStatusHistoryRepository history;
    private final OrderAuditRepository audits;
    private final OutboxEventWriter events;
    private final EntityManager entityManager;
    private final OrderIdempotencyRepository idempotency;

    public OrderService(OrderRepository orders, OrderItemRepository items, OrderAddressRepository addresses, OrderStatusHistoryRepository history, OrderAuditRepository audits, OutboxEventWriter events, OrderIdempotencyRepository idempotency, EntityManager entityManager) {
        this.orders = orders;
        this.items = items;
        this.addresses = addresses;
        this.history = history;
        this.audits = audits;
        this.events = events;
        this.entityManager = entityManager;
        this.idempotency = idempotency;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, String idempotencyKey, String changedBy) {
        String requestHash = hash(request.toString());
        Optional<OrderIdempotency> existing = idempotency.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            if (!existing.get().getRequestHash().equals(requestHash)) throw new IdempotencyConflictException();
            if (existing.get().getOrderId() != null) return get(existing.get().getOrderId());
            throw new IdempotencyConflictException();
        }
        OrderIdempotency operation = idempotency.saveAndFlush(new OrderIdempotency(idempotencyKey, requestHash));
        validateItemAmounts(request.items());
        Order order = orders.save(new Order(request.customerId(), request.currencyCode(), money(request.shippingAmount())));
        for (CreateOrderItemRequest item : request.items()) items.save(toItem(order, item));
        addresses.save(toAddress(order, AddressType.SHIPPING, request.shippingAddress()));
        if (request.billingAddress() != null)
            addresses.save(toAddress(order, AddressType.BILLING, request.billingAddress()));
        recalculate(order);
        history.save(new OrderStatusHistory(order, null, OrderStatus.CREATED, "Order created", changedBy));
        String correlationId = correlation();
        audit(order, AuditAction.ORDER_CREATED, "Order", null, null, OrderStatus.CREATED.name(), changedBy, correlationId);
        event(order, OrderEventType.ORDER_CREATED, correlationId, Map.of("orderStatus", order.getOrderStatus().name(), "totalAmount", order.getTotalAmount()));
        OrderResponse response = response(order);
        operation.complete(order.getOrderId(), Map.of("orderId", order.getOrderId().toString()));
        idempotency.save(operation);
        return response;
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID orderId) {
        return response(find(orderId));
    }

    @Transactional
    public OrderResponse update(UUID orderId, UpdateOrderRequest request, String changedBy) {
        Order order = editable(find(orderId));
        String correlationId = correlation();
        order.update(request.currencyCode(), money(request.shippingAmount()));
        recalculate(order);
        audit(order, AuditAction.ORDER_UPDATED, "Order", null, null, null, changedBy, correlationId);
        event(order, OrderEventType.ORDER_UPDATED, correlationId, Map.of("totalAmount", order.getTotalAmount()));
        return response(order);
    }

    @Transactional
    public OrderResponse addItem(UUID orderId, AddOrderItemRequest request, String changedBy) {
        Order order = editable(find(orderId));
        validateItemAmount(request.quantity(), request.unitPrice(), request.discountAmount(), request.taxAmount());
        OrderItem item = items.save(new OrderItem(order, request.productId(), request.sku(), request.productName(), request.quantity(), money(request.unitPrice()), money(request.discountAmount()), money(request.taxAmount())));
        recalculate(order);
        String correlationId = correlation();
        audit(order, AuditAction.ORDER_ITEM_ADDED, "OrderItem", null, null, item.getOrderItemId().toString(), changedBy, correlationId);
        event(order, OrderEventType.ORDER_ITEM_ADDED, correlationId, Map.of("orderItemId", item.getOrderItemId().toString()));
        return response(order);
    }

    @Transactional
    public OrderResponse updateItem(UUID orderId, UUID itemId, UpdateOrderItemRequest request, String changedBy) {
        Order order = editable(find(orderId));
        validateItemAmount(request.quantity(), request.unitPrice(), request.discountAmount(), request.taxAmount());
        OrderItem item = items.findByOrderItemIdAndOrderOrderId(itemId, orderId).orElseThrow(() -> new OrderItemNotFoundException(itemId));
        item.update(request.productId(), request.sku(), request.productName(), request.quantity(), money(request.unitPrice()), money(request.discountAmount()), money(request.taxAmount()));
        recalculate(order);
        String correlationId = correlation();
        audit(order, AuditAction.ORDER_ITEM_UPDATED, "OrderItem", null, null, itemId.toString(), changedBy, correlationId);
        event(order, OrderEventType.ORDER_ITEM_UPDATED, correlationId, Map.of("orderItemId", itemId.toString()));
        return response(order);
    }

    @Transactional
    public OrderResponse removeItem(UUID orderId, UUID itemId, String changedBy) {
        Order order = editable(find(orderId));
        OrderItem item = items.findByOrderItemIdAndOrderOrderId(itemId, orderId).orElseThrow(() -> new OrderItemNotFoundException(itemId));
        if (items.findByOrderOrderIdOrderById(orderId).size() == 1)
            throw new IllegalArgumentException("An order must contain at least one item");
        items.delete(item);
        items.flush();
        recalculate(order);
        String correlationId = correlation();
        audit(order, AuditAction.ORDER_ITEM_REMOVED, "OrderItem", null, itemId.toString(), null, changedBy, correlationId);
        event(order, OrderEventType.ORDER_ITEM_REMOVED, correlationId, Map.of("orderItemId", itemId.toString()));
        return response(order);
    }

    @Transactional
    public OrderResponse updateAddress(UUID orderId, AddressType type, UpdateOrderAddressRequest request, String changedBy) {
        Order order = editable(find(orderId));
        OrderAddress address = addresses.findByOrderOrderIdAndAddressType(orderId, type).orElseGet(() -> new OrderAddress(order, type, request.customerAddressId(), request.addressLine1(), request.addressLine2(), request.city(), request.state(), request.postalCode(), request.countryCode()));
        address.update(request.customerAddressId(), request.addressLine1(), request.addressLine2(), request.city(), request.state(), request.postalCode(), request.countryCode());
        addresses.save(address);
        String correlationId = correlation();
        audit(order, AuditAction.ORDER_ADDRESS_UPDATED, "OrderAddress", "addressType", null, type.name(), changedBy, correlationId);
        event(order, OrderEventType.ORDER_UPDATED, correlationId, Map.of("addressType", type.name()));
        return response(order);
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request, String changedBy) {
        Order order = find(orderId);
        transition(order, request.newStatus(), request.changeReason(), changedBy);
        return response(order);
    }

    @Transactional
    public OrderResponse cancel(UUID orderId, CancelOrderRequest request, String changedBy) {
        Order order = find(orderId);
        if (!Set.of(OrderStatus.CREATED, OrderStatus.PENDING_PAYMENT, OrderStatus.CONFIRMED).contains(order.getOrderStatus()))
            throw new InvalidOrderStatusTransitionException("Order cannot be cancelled from " + order.getOrderStatus());
        transition(order, OrderStatus.CANCELLED, request.reason(), changedBy);
        return response(order);
    }

    private void transition(Order order, OrderStatus next, String reason, String changedBy) {
        OrderStatus previous = order.getOrderStatus();
        if (!allowed(previous, next))
            throw new InvalidOrderStatusTransitionException("Invalid order status transition: " + previous + " -> " + next);
        order.transition(next);
        history.save(new OrderStatusHistory(order, previous, next, reason, changedBy));
        String correlationId = correlation();
        AuditAction action = next == OrderStatus.CANCELLED ? AuditAction.ORDER_CANCELLED : AuditAction.ORDER_STATUS_CHANGED;
        audit(order, action, "Order", "orderStatus", previous.name(), next.name(), changedBy, correlationId);
        OrderEventType type = switch (next) {
            case CONFIRMED -> OrderEventType.ORDER_CONFIRMED;
            case CANCELLED -> OrderEventType.ORDER_CANCELLED;
            case FAILED -> OrderEventType.ORDER_FAILED;
            default -> OrderEventType.ORDER_UPDATED;
        };
        event(order, type, correlationId, Map.of("previousStatus", previous.name(), "orderStatus", next.name()));
    }

    private boolean allowed(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case CREATED -> Set.of(OrderStatus.PENDING_PAYMENT, OrderStatus.CANCELLED, OrderStatus.FAILED).contains(to);
            case PENDING_PAYMENT ->
                    Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED, OrderStatus.FAILED).contains(to);
            case CONFIRMED -> Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED, OrderStatus.FAILED).contains(to);
            case PROCESSING -> Set.of(OrderStatus.COMPLETED, OrderStatus.FAILED).contains(to);
            default -> false;
        };
    }

    private Order editable(Order order) {
        if (!Set.of(OrderStatus.CREATED, OrderStatus.PENDING_PAYMENT).contains(order.getOrderStatus()))
            throw new OrderModificationNotAllowedException(order.getOrderStatus().name());
        return order;
    }

    private void recalculate(Order order) {
        List<OrderItem> orderItems = items.findByOrderOrderIdOrderById(order.getOrderId());
        BigDecimal subtotal = orderItems.stream().map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = orderItems.stream().map(OrderItem::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = orderItems.stream().map(OrderItem::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.totals(money(subtotal), money(discount), money(tax), money(subtotal.subtract(discount).add(tax).add(order.getShippingAmount())));
    }

    private OrderItem toItem(Order order, CreateOrderItemRequest item) {
        return new OrderItem(order, item.productId(), item.sku(), item.productName(), item.quantity(), money(item.unitPrice()), money(item.discountAmount()), money(item.taxAmount()));
    }

    private OrderAddress toAddress(Order order, AddressType type, CreateOrderAddressRequest address) {
        return new OrderAddress(order, type, address.customerAddressId(), address.addressLine1(), address.addressLine2(), address.city(), address.state(), address.postalCode(), address.countryCode());
    }

    private void validateItemAmounts(List<CreateOrderItemRequest> list) {
        list.forEach(i -> validateItemAmount(i.quantity(), i.unitPrice(), i.discountAmount(), i.taxAmount()));
    }

    private void validateItemAmount(int quantity, BigDecimal price, BigDecimal discount, BigDecimal tax) {
        if (quantity <= 0 || price.signum() < 0 || discount.signum() < 0 || tax.signum() < 0)
            throw new IllegalArgumentException("Item quantity and amounts are invalid");
        if (discount.compareTo(price.multiply(BigDecimal.valueOf(quantity))) > 0)
            throw new IllegalArgumentException("Item discount cannot exceed gross amount");
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private Order find(UUID id) {
        return orders.findByOrderId(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private void audit(Order order, AuditAction action, String type, String field, String oldValue, String newValue, String changedBy, String correlationId) {
        audits.save(new OrderAudit(order, action, type, field, oldValue, newValue, changedBy, correlationId));
    }

    private void event(Order order, OrderEventType type, String correlationId, Map<String, Object> payload) {
        // Flush JPA writes before the JDBC event insert checks the order foreign key.
        entityManager.flush();
        events.record(new OutboxEventRequest(order.getOrderId(), type.name(), payload, correlationId));
    }

    private String correlation() {
        return UUID.randomUUID().toString();
    }

    private String hash(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private OrderResponse response(Order order) {
        List<OrderItemResponse> itemResponses = items.findByOrderOrderIdOrderById(order.getOrderId()).stream().map(i -> new OrderItemResponse(i.getOrderItemId(), i.getProductId(), i.getSku(), i.getProductName(), i.getQuantity(), i.getUnitPrice(), i.getDiscountAmount(), i.getTaxAmount(), i.getTotalAmount(), i.getCreatedAt(), i.getUpdatedAt())).toList();
        Map<AddressType, OrderAddressResponse> addressMap = new EnumMap<>(AddressType.class);
        addresses.findByOrderOrderIdOrderById(order.getOrderId()).forEach(a -> addressMap.put(a.getAddressType(), new OrderAddressResponse(a.getOrderAddressId(), a.getCustomerAddressId(), a.getAddressType(), a.getAddressLine1(), a.getAddressLine2(), a.getCity(), a.getState(), a.getPostalCode(), a.getCountryCode(), a.getCreatedAt())));
        return new OrderResponse(order.getOrderId(), order.getCustomerId(), order.getOrderStatus(), order.getCurrencyCode(), order.getSubtotalAmount(), order.getDiscountAmount(), order.getTaxAmount(), order.getShippingAmount(), order.getTotalAmount(), itemResponses, addressMap.get(AddressType.SHIPPING), addressMap.get(AddressType.BILLING), order.getCreatedAt(), order.getUpdatedAt());
    }
}
