package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Invoice {

    private final String id;
    private final String number;
    private final String orderId;
    private final String buyerId;
    private final BigDecimal total;
    private InvoiceStatus status;
    private final Instant issuedAt;

    public Invoice(
            String id,
            String number,
            String orderId,
            String buyerId,
            BigDecimal total,
            InvoiceStatus status,
            Instant issuedAt) {
        this.id = RequiredText.require(id, "El identificador de la factura es obligatorio");
        this.number = RequiredText.require(number, "El número de factura es obligatorio");
        this.orderId = RequiredText.require(orderId, "La factura debe estar asociada a un pedido");
        this.buyerId = RequiredText.require(buyerId, "La factura debe estar asociada a un comprador");
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El total de la factura debe ser mayor que cero");
        }
        if (status == null) {
            throw new BusinessRuleException("El estado de la factura es obligatorio");
        }
        this.total = total;
        this.status = status;
        this.issuedAt = issuedAt == null ? Instant.now() : issuedAt;
    }

    public static Invoice issue(Order order) {
        String id = UUID.randomUUID().toString();
        String number = "FAC-" + id.substring(0, 8).toUpperCase();
        return new Invoice(id, number, order.getId(), order.getBuyerId(), order.total(), InvoiceStatus.ISSUED, Instant.now());
    }

    public void voidInvoice() {
        if (status == InvoiceStatus.VOIDED) {
            throw new BusinessRuleException("La factura ya está anulada");
        }
        this.status = InvoiceStatus.VOIDED;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }
}
