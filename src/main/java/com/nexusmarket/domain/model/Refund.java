package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.math.BigDecimal;

public class Refund {

    private final String id;
    private final String returnId;
    private final String invoiceId;
    private final BigDecimal amount;
    private RefundStatus status;

    public Refund(String id, String returnId, String invoiceId, BigDecimal amount, RefundStatus status) {
        this.id = RequiredText.require(id, "El identificador del reembolso es obligatorio");
        this.returnId = RequiredText.require(returnId, "El reembolso debe estar asociado a una devolución");
        this.invoiceId = RequiredText.require(invoiceId, "El reembolso debe estar asociado a una factura");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El monto del reembolso debe ser mayor que cero");
        }
        if (status == null) {
            throw new BusinessRuleException("El estado del reembolso es obligatorio");
        }
        this.amount = amount;
        this.status = status;
    }

    public void process() {
        if (status != RefundStatus.PENDING) {
            throw new BusinessRuleException("El reembolso ya fue procesado");
        }
        this.status = RefundStatus.PROCESSED;
    }

    public String getId() {
        return id;
    }

    public String getReturnId() {
        return returnId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
    }
}
