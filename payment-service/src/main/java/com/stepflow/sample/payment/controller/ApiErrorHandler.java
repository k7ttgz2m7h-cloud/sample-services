package com.stepflow.sample.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handle(ResponseStatusException exception) {
        int status = exception.getStatusCode().value();
        String message = exception.getReason() != null ? exception.getReason() : "Request failed";
        return ResponseEntity.status(status).body(new ApiError(errorCode(message, status), message, status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "Internal server error", 500));
    }

    private String errorCode(String message, int status) {
        String normalized = message.replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_|_$", "")
                .toUpperCase();
        return !normalized.isBlank() ? normalized : "HTTP_" + status;
    }

    public record ApiError(String code, String message, int status) {
    }
}
