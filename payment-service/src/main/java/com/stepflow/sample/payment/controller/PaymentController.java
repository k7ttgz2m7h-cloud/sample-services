package com.stepflow.sample.payment.controller;

import com.stepflow.sample.payment.dto.PaymentDtos.*;
import com.stepflow.sample.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentResponse> authorize(@RequestBody AuthorizeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.authorize(request));
    }

    @PostMapping("/authorizations/void")
    public PaymentResponse voidAuthorization(@RequestBody VoidRequest request) {
        return service.voidAuthorization(request);
    }

    @PostMapping("/capture")
    public PaymentResponse capture(@RequestBody CaptureRequest request) {
        return service.capture(request);
    }

    @PostMapping("/refund")
    public PaymentResponse refund(@RequestBody RefundRequest request) {
        return service.refund(request);
    }
}
