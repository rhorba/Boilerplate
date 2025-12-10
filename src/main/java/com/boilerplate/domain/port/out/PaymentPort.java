package com.boilerplate.domain.port.out;

import java.math.BigDecimal;

public interface PaymentPort {
    String createPayment(BigDecimal amount, String currency, String description);
}
