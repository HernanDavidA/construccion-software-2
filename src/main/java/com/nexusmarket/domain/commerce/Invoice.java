package com.nexusmarket.domain.commerce;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Commercial information associated with a paid purchase (OBJ-09).
 * Number and amount are the coherent minimum: the spec does not detail invoice attributes.
 */
public class Invoice {

    private final String number;
    private final Order order;
    private final LocalDateTime issuedAt;
    private final BigDecimal totalAmount;

    public Invoice(String number, Order order) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("number is required");
        }
        this.number = number.trim();
        this.order = Objects.requireNonNull(order, "order is required");
        this.issuedAt = LocalDateTime.now();
        this.totalAmount = order.getTotal();
    }

    public String getNumber() {
        return number;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
