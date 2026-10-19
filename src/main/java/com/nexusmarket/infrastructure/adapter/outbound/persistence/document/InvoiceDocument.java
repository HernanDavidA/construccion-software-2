package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.model.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class InvoiceDocument {

    @Id
    private String id;
    private String number;
    private String orderId;
    private String buyerId;
    private BigDecimal total;
    private InvoiceStatus status;
    private Instant issuedAt;

    public static InvoiceDocument from(Invoice invoice) {
        return new InvoiceDocument(invoice.getId(), invoice.getNumber(), invoice.getOrderId(), invoice.getBuyerId(), invoice.getTotal(), invoice.getStatus(), invoice.getIssuedAt());
    }

    public Invoice toDomain() {
        return new Invoice(id, number, orderId, buyerId, total, status, issuedAt);
    }
}
