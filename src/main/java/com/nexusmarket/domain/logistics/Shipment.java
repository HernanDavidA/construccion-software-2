package com.nexusmarket.domain.logistics;

import com.nexusmarket.domain.commerce.Order;
import com.nexusmarket.domain.users.LogisticsOperator;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Packing, dispatch and transport of an order with physical products (OBJ-10).
 */
public class Shipment {

    private final String id;
    private final Order order;
    private final LogisticsOperator operator;
    private ShipmentStatus status;
    private final LocalDateTime createdAt;
    private String trackingNumber;

    public Shipment(String id, Order order, LogisticsOperator operator) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        this.id = id.trim();
        this.order = Objects.requireNonNull(order, "order is required");
        this.operator = Objects.requireNonNull(operator, "operator is required");
        if (!order.requiresShipment()) {
            throw new IllegalStateException("a digital-only order does not require shipment");
        }
        this.status = ShipmentStatus.IN_PREPARATION;
        this.createdAt = LocalDateTime.now();
        order.attachShipment(this);
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public LogisticsOperator getOperator() {
        return operator;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void ship(String trackingNumber) {
        if (status != ShipmentStatus.IN_PREPARATION) {
            throw new IllegalStateException("the shipment is not in preparation");
        }
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("trackingNumber is required");
        }
        this.trackingNumber = trackingNumber.trim();
        this.status = ShipmentStatus.SHIPPED;
        order.ship();
    }

    public void markInTransit() {
        if (status != ShipmentStatus.SHIPPED) {
            throw new IllegalStateException("the shipment must be shipped");
        }
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void confirmDelivery() {
        if (status != ShipmentStatus.IN_TRANSIT && status != ShipmentStatus.SHIPPED) {
            throw new IllegalStateException("the shipment cannot be confirmed in the current status");
        }
        this.status = ShipmentStatus.DELIVERED;
        order.complete();
    }
}
