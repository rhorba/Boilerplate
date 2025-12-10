package com.boilerplate.infrastructure.adapter.out.payment;

import com.boilerplate.domain.port.out.PaymentPort;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service("paypalService")
public class PaypalAdapter implements PaymentPort {

    @Override
    public String createPayment(BigDecimal amount, String currency, String description) {
        // Mock implementation
        // In reality: Call PayPal v2/checkout/orders
        return "https://www.sandbox.paypal.com/checkoutnow?token=mock_token_for_" + amount;
    }
}
