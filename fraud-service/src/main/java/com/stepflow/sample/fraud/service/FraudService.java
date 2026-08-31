package com.stepflow.sample.fraud.service;

import com.stepflow.sample.fraud.dto.FraudDtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FraudService {
    public FraudScreenResponse screen(FraudScreenRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        return new FraudScreenResponse(request.orderId(), "APPROVED", 10);
    }
}
