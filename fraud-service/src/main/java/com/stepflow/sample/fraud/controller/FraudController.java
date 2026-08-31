package com.stepflow.sample.fraud.controller;

import com.stepflow.sample.fraud.dto.FraudDtos.*;
import com.stepflow.sample.fraud.service.FraudService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fraud")
public class FraudController {
    private final FraudService service;

    public FraudController(FraudService service) {
        this.service = service;
    }

    @PostMapping("/screen")
    public FraudScreenResponse screen(@RequestBody FraudScreenRequest request) {
        return service.screen(request);
    }
}
