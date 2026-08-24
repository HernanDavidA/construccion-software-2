package com.nexusmarket.domain.logistics;

import com.nexusmarket.domain.commerce.Order;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Post-sale request linked to an order (OBJ-11).
 */
public class ReturnRequest {

    private final String id;
    private final Order order;
    private final String reason;
    private ReturnStatus status;
    private final LocalDateTime requestedAt;
    private Refund refund;

    public ReturnRequest(String id, Order order, String reason) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        this.id = id.trim();
        this.order = Objects.requireNonNull(order, "order is required");
        this.reason = reason.trim();
        this.status = ReturnStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
        order.registerReturn(this);
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public String getReason() {
        return reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public Refund getRefund() {
        return refund;
    }

    public void approve() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("only a requested return can be approved");
        }
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        if (status != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("only a requested return can be rejected");
        }
        this.status = ReturnStatus.REJECTED;
    }

    public Refund createRefund(String refundId) {
        if (status != ReturnStatus.APPROVED) {
            throw new IllegalStateException("the refund requires an approved return");
        }
        if (refund != null) {
            throw new IllegalStateException("the return already has a refund");
        }
        this.refund = new Refund(refundId, this);
        return refund;
    }

    public void complete() {
        if (status != ReturnStatus.APPROVED) {
            throw new IllegalStateException("only an approved return can be completed");
        }
        this.status = ReturnStatus.COMPLETED;
    }
}
