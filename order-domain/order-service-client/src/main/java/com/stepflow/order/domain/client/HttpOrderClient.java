package com.stepflow.order.domain.client;

import com.stepflow.order.domain.client.dto.OrderRequests.*;
import com.stepflow.order.domain.client.dto.OrderResponses.OrderResponse;
import com.stepflow.order.domain.client.dto.OrderEnums.AddressType;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;
import java.util.UUID;

/**
 * HTTP adapter. Authentication, interceptors and timeouts are configured by the caller.
 */
public class HttpOrderClient implements OrderClient {
    private final RestClient restClient;

    public HttpOrderClient(RestClient.Builder builder, String baseUrl) {
        Objects.requireNonNull(builder, "builder is required");
        requireText(baseUrl, "baseUrl");
        this.restClient = builder.clone().baseUrl(baseUrl).build();
    }

    public HttpOrderClient(RestClient restClient) {
        this.restClient = Objects.requireNonNull(restClient, "restClient is required");
    }

    private static void requireOrderId(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId is required");
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is required");
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey, String changedBy) {
        Objects.requireNonNull(request, "request is required");
        requireText(idempotencyKey, "idempotencyKey");
        return execute(audit(restClient.post().uri("/orders")
                .header("Idempotency-Key", idempotencyKey), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse getOrder(UUID orderId) {
        requireOrderId(orderId);
        return execute(restClient.get().uri("/orders/{orderId}", orderId));
    }

    @Override
    public OrderResponse updateOrder(UUID orderId, UpdateOrderRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.put().uri("/orders/{orderId}", orderId), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse addItem(UUID orderId, AddOrderItemRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.post().uri("/orders/{orderId}/items", orderId), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse updateItem(UUID orderId, UUID orderItemId, UpdateOrderItemRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(orderItemId, "orderItemId is required");
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.put().uri("/orders/{orderId}/items/{orderItemId}", orderId, orderItemId), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse removeItem(UUID orderId, UUID orderItemId, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(orderItemId, "orderItemId is required");
        var request = restClient.delete().uri("/orders/{orderId}/items/{orderItemId}", orderId, orderItemId);
        if (changedBy != null) {
            requireText(changedBy, "changedBy");
            request.header("X-Changed-By", changedBy);
        }
        return execute(request);
    }

    @Override
    public OrderResponse updateAddress(UUID orderId, AddressType addressType,
                                       UpdateOrderAddressRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(addressType, "addressType is required");
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.put().uri("/orders/{orderId}/addresses/{addressType}",
                orderId, addressType.name()), changedBy).contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.put().uri("/orders/{orderId}/status", orderId), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    @Override
    public OrderResponse cancelOrder(UUID orderId, CancelOrderRequest request, String changedBy) {
        requireOrderId(orderId);
        Objects.requireNonNull(request, "request is required");
        return execute(audit(restClient.post().uri("/orders/{orderId}/cancel", orderId), changedBy)
                .contentType(MediaType.APPLICATION_JSON).body(request));
    }

    private RestClient.RequestBodySpec audit(RestClient.RequestBodySpec request, String changedBy) {
        if (changedBy != null) {
            requireText(changedBy, "changedBy");
            request.header("X-Changed-By", changedBy);
        }
        return request;
    }

    private OrderResponse execute(RestClient.RequestHeadersSpec<?> request) {
        try {
            var response = request.accept(MediaType.APPLICATION_JSON).retrieve().toEntity(OrderResponse.class);
            if (response.getBody() == null) {
                throw new OrderClientException(response.getStatusCode(), "",
                        "Order Service returned an empty response", null);
            }
            return response.getBody();
        } catch (RestClientResponseException exception) {
            throw new OrderClientException(exception.getStatusCode(), exception.getResponseBodyAsString(),
                    "Order Service request failed with status " + exception.getStatusCode().value(), exception);
        } catch (RestClientException exception) {
            throw new OrderClientException(null, null, "Order Service request could not be completed", exception);
        }
    }
}
