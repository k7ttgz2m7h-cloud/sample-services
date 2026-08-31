package com.stepflow.sample.payment.service;

import com.stepflow.sample.payment.dto.PaymentDtos.*;
import com.stepflow.sample.payment.entity.Payment;
import com.stepflow.sample.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public PaymentResponse authorize(AuthorizeRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (repository.findByOrderId(request.orderId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment authorization already exists for order: " + request.orderId());
        }
        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setAuthorizationId("AUTH-" + request.orderId());
        payment.setAmount(request.amount());
        if (payment.getStatus() == null || Boolean.TRUE.equals(payment.getCompensated())) {
            payment.setStatus("AUTHORIZED");
            payment.setCompensated(false);
            payment.setCompensatedAt(null);
            payment.setCompensationReason(null);
        }
        repository.save(payment);
        return new PaymentResponse(payment.getAuthorizationId(), null, payment.getOrderId(), payment.getStatus());
    }

    public PaymentResponse voidAuthorization(VoidRequest request) {
        Payment payment = repository.findByAuthorizationId(request.authorizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authorization not found: " + request.authorizationId()));
        if (!Boolean.TRUE.equals(payment.getCompensated())) {
            payment.setStatus("VOIDED");
            markCompensated(payment, "BUSINESS_PROCESS_COMPENSATION");
        }
        repository.save(payment);
        return new PaymentResponse(payment.getAuthorizationId(), payment.getCaptureId(), payment.getOrderId(), payment.getStatus());
    }

    public PaymentResponse capture(CaptureRequest request) {
        Payment payment = repository.findByAuthorizationId(request.authorizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authorization not found: " + request.authorizationId()));
        if ("CAPTURED".equalsIgnoreCase(payment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment already captured for authorization: " + request.authorizationId());
        }
        payment.setCaptureId("CAP-" + request.orderId());
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setStatus("CAPTURED");
        repository.save(payment);
        return new PaymentResponse(payment.getAuthorizationId(), payment.getCaptureId(), payment.getOrderId(), payment.getStatus());
    }

    public PaymentResponse refund(RefundRequest request) {
        Payment payment = repository.findByCaptureId(request.captureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Capture not found: " + request.captureId()));
        if (!Boolean.TRUE.equals(payment.getCompensated())) {
            payment.setStatus("REFUNDED");
            markCompensated(payment, "BUSINESS_PROCESS_COMPENSATION");
        }
        repository.save(payment);
        return new PaymentResponse(payment.getAuthorizationId(), payment.getCaptureId(), payment.getOrderId(), payment.getStatus());
    }

    private void markCompensated(Payment payment, String reason) {
        payment.setCompensated(true);
        payment.setCompensatedAt(LocalDateTime.now());
        payment.setCompensationReason(reason);
    }
}
