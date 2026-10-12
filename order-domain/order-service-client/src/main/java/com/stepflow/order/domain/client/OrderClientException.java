package com.stepflow.order.domain.client;

import org.springframework.http.HttpStatusCode;

/**
 * Preserves HTTP error details; statusCode is null for transport or response-decoding failures.
 */
public class OrderClientException extends RuntimeException {
    private final HttpStatusCode statusCode;
    private final String responseBody;

    public OrderClientException(HttpStatusCode statusCode, String responseBody, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
