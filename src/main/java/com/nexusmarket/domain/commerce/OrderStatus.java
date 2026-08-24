package com.nexusmarket.domain.commerce;

/**
 * Order lifecycle. A completed order cannot be modified.
 */
public enum OrderStatus {
    CART,
    PENDING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED_COMPLETED
}
