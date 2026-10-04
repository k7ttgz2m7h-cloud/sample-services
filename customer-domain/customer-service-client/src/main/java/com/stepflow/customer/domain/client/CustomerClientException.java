package com.stepflow.customer.domain.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientResponseException;

public class CustomerClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public CustomerClientException(HttpStatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    static CustomerClientException from(RestClientResponseException exception) {
        return new CustomerClientException(
                exception.getStatusCode(),
                "Customer Service request failed with status " + exception.getStatusCode().value(),
                exception);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
