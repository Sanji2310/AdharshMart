package com.adharsh.adharshmart.service;

import java.math.BigDecimal;

/**
 * Strategy pattern — swappable payment/notification channel. The scope constraints forbid a real
 * payment gateway, so {@link MockPaymentStrategy} is the only implementation today, but checkout
 * depends on this interface rather than that class.
 */
public interface PaymentStrategy {
    PaymentResult confirm(BigDecimal amount);

    record PaymentResult(boolean approved, String reference) {
    }
}
