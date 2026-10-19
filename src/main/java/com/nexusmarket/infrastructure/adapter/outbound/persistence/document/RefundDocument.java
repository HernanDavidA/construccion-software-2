package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.model.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refunds")
public class RefundDocument {

    @Id
    private String id;
    private String returnId;
    private String invoiceId;
    private BigDecimal amount;
    private RefundStatus status;

    public static RefundDocument from(Refund refund) {
        return new RefundDocument(refund.getId(), refund.getReturnId(), refund.getInvoiceId(), refund.getAmount(), refund.getStatus());
    }

    public Refund toDomain() {
        return new Refund(id, returnId, invoiceId, amount, status);
    }
}
