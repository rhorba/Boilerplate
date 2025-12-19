package com.boilerplate.domain.port.out;

public interface PaymentPort {
    String createCheckoutSession(String priceId, String successUrl, String cancelUrl);
}
