package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderLine;
import com.nexusmarket.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;
    private String buyerId;
    private OrderStatus status;
    private List<OrderLineDocument> lines;
    private Instant createdAt;

    public static OrderDocument from(Order order) {
        List<OrderLineDocument> stored = order.getLines().stream()
                .map(line -> new OrderLineDocument(line.productId(), line.variantId(), line.warehouseId(), line.quantity(), line.unitPrice(), line.productType()))
                .toList();
        return new OrderDocument(order.getId(), order.getBuyerId(), order.getStatus(), stored, order.getCreatedAt());
    }

    public Order toDomain() {
        List<OrderLine> domainLines = lines.stream()
                .map(line -> new OrderLine(line.getProductId(), line.getVariantId(), line.getWarehouseId(), line.getQuantity(), line.getUnitPrice(), line.getProductType()))
                .toList();
        return new Order(id, buyerId, status, domainLines, createdAt);
    }
}
