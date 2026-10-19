package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.time.Instant;

public class ReturnRequest {

    private final String id;
    private final String orderId;
    private final String buyerId;
    private final String reason;
    private ReturnStatus status;
    private final Instant requestedAt;

    public ReturnRequest(
            String id,
            String orderId,
            String buyerId,
            String reason,
            ReturnStatus status,
            Instant requestedAt) {
        this.id = RequiredText.require(id, "El identificador de la devolución es obligatorio");
        this.orderId = RequiredText.require(orderId, "La devolución debe estar asociada a un pedido");
        this.buyerId = RequiredText.require(buyerId, "La devolución debe estar asociada a un comprador");
        this.reason = RequiredText.require(reason, "El motivo de la devolución es obligatorio");
        if (status == null) {
            throw new BusinessRuleException("El estado de la devolución es obligatorio");
        }
        this.status = status;
        this.requestedAt = requestedAt == null ? Instant.now() : requestedAt;
    }

    public void approve() {
        if (status != ReturnStatus.REQUESTED) {
            throw new BusinessRuleException("Solo se puede aprobar una devolución solicitada");
        }
        this.status = ReturnStatus.APPROVED;
    }

    public void reject() {
        if (status != ReturnStatus.REQUESTED) {
            throw new BusinessRuleException("Solo se puede rechazar una devolución solicitada");
        }
        this.status = ReturnStatus.REJECTED;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getReason() {
        return reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }
}
