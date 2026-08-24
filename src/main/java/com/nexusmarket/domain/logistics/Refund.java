package com.nexusmarket.domain.logistics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Refund linked to a return. Managed by buyer and administrator (OBJ-11).
 */
public class Refund {

    private final String id;
    private final ReturnRequest returnRequest;
    private final BigDecimal amount;
    private RefundStatus status;
    private final LocalDateTime createdAt;

    public Refund(String id, ReturnRequest returnRequest) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        this.id = id.trim();
        this.returnRequest = Objects.requireNonNull(returnRequest, "returnRequest is required");
        this.amount = returnRequest.getOrder().getTotal();
        this.status = RefundStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public ReturnRequest getReturnRequest() {
        return returnRequest;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void process() {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("only a pending refund can be processed");
        }
        this.status = RefundStatus.PROCESSED;
        returnRequest.complete();
    }

    public void reject() {
        if (status != RefundStatus.PENDING) {
            throw new IllegalStateException("only a pending refund can be rejected");
        }
        this.status = RefundStatus.REJECTED;
    }
}
