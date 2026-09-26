package com.adharsh.adharshmart.model;

/** O2 order-status workflow: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, or CANCELLED. */
public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
