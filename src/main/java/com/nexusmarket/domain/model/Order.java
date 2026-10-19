package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {

    private final String id;
    private final String buyerId;
    private OrderStatus status;
    private final List<OrderLine> lines;
    private final Instant createdAt;

    public Order(String id, String buyerId, OrderStatus status, List<OrderLine> lines, Instant createdAt) {
        this.id = RequiredText.require(id, "El identificador del pedido es obligatorio");
        this.buyerId = RequiredText.require(buyerId, "El pedido debe pertenecer a un comprador");
        if (status == null) {
            throw new BusinessRuleException("El estado del pedido es obligatorio");
        }
        if (lines == null || lines.isEmpty()) {
            throw new BusinessRuleException("El pedido debe contener al menos un producto");
        }
        this.status = status;
        this.lines = List.copyOf(lines);
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public void advanceTo(OrderStatus target) {
        if (status == OrderStatus.COMPLETED) {
            throw new BusinessRuleException("Un pedido finalizado no podrá ser modificado");
        }
        boolean allowed = switch (status) {
            case PENDING_PAYMENT -> target == OrderStatus.PAID;
            case PAID -> target == OrderStatus.SHIPPED || (target == OrderStatus.COMPLETED && !containsPhysicalProduct());
            case SHIPPED -> target == OrderStatus.COMPLETED;
            case COMPLETED -> false;
        };
        if (!allowed) {
            throw new BusinessRuleException("Transición de pedido no permitida: " + status + " hacia " + target);
        }
        this.status = target;
    }

    public boolean containsPhysicalProduct() {
        return lines.stream().anyMatch(line -> line.productType() == ProductType.PHYSICAL);
    }

    public BigDecimal total() {
        return lines.stream().map(OrderLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getId() {
        return id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
