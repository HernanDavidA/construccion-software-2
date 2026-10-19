package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "returns")
public class ReturnRequestDocument {

    @Id
    private String id;
    private String orderId;
    private String buyerId;
    private String reason;
    private ReturnStatus status;
    private Instant requestedAt;

    public static ReturnRequestDocument from(ReturnRequest returnRequest) {
        return new ReturnRequestDocument(
                returnRequest.getId(),
                returnRequest.getOrderId(),
                returnRequest.getBuyerId(),
                returnRequest.getReason(),
                returnRequest.getStatus(),
                returnRequest.getRequestedAt());
    }

    public ReturnRequest toDomain() {
        return new ReturnRequest(id, orderId, buyerId, reason, status, requestedAt);
    }
}
