package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.domain.port.out.PaymentPort;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentPort paymentPort;

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody CheckoutRequest request) {
        String url = paymentPort.createCheckoutSession(request.getPriceId(), request.getSuccessUrl(),
                request.getCancelUrl());
        return ResponseEntity.ok(Map.of("url", url));
    }

    @Data
    public static class CheckoutRequest {
        private String priceId;
        private String successUrl;
        private String cancelUrl;
    }
}
