package com.adharsh.adharshmart.service;

import java.math.BigDecimal;
import java.util.UUID;

/** The only checkout path allowed by the scope constraints — always confirms, no external gateway. */
public class MockPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult confirm(BigDecimal amount) {
        return new PaymentResult(true, "MOCK-" + UUID.randomUUID());
    }
}
