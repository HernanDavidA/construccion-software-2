package com.nexusmarket.domain.commerce;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductVariant;
import com.nexusmarket.domain.logistics.ReturnRequest;
import com.nexusmarket.domain.logistics.Shipment;
import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Formal commercial commitment and central process of the marketplace (OBJ-08).
 */
public class Order {

    private final String id;
    private final Buyer buyer;
    private final Address deliveryAddress;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private final List<OrderItem> items;
    private Invoice invoice;
    private Shipment shipment;
    private final List<ReturnRequest> returns;

    public Order(String id, Buyer buyer, Address deliveryAddress) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        this.id = id.trim();
        this.buyer = Objects.requireNonNull(buyer, "buyer is required");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress is required");
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CART;
        this.items = new ArrayList<>();
        this.returns = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void attachShipment(Shipment shipment) {
        requireNotCompleted();
        this.shipment = Objects.requireNonNull(shipment, "shipment is required");
    }

    public List<ReturnRequest> getReturns() {
        return Collections.unmodifiableList(returns);
    }

    public void registerReturn(ReturnRequest returnRequest) {
        if (status != OrderStatus.SHIPPED && status != OrderStatus.DELIVERED_COMPLETED) {
            throw new IllegalStateException("returns can only be requested for shipped or delivered orders");
        }
        returns.add(Objects.requireNonNull(returnRequest, "returnRequest is required"));
    }

    public void addItem(Product product, ProductVariant variant, int quantity) {
        requireNotCompleted();
        if (status != OrderStatus.CART) {
            throw new IllegalStateException("items can only be added in cart status");
        }
        items.add(new OrderItem(product, variant, quantity));
    }

    public void confirm() {
        requireNotCompleted();
        if (items.isEmpty()) {
            throw new IllegalStateException("the order has no items");
        }
        transition(OrderStatus.CART, OrderStatus.PENDING_PAYMENT);
    }

    public Invoice registerPayment(String invoiceNumber) {
        requireNotCompleted();
        transition(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        this.invoice = new Invoice(invoiceNumber, this);
        return invoice;
    }

    public void ship() {
        requireNotCompleted();
        transition(OrderStatus.PAID, OrderStatus.SHIPPED);
    }

    public void complete() {
        requireNotCompleted();
        transition(OrderStatus.SHIPPED, OrderStatus.DELIVERED_COMPLETED);
    }

    public void completeDigitalDelivery() {
        requireNotCompleted();
        if (requiresShipment()) {
            throw new IllegalStateException("a physical order is completed with logistics delivery");
        }
        transition(OrderStatus.PAID, OrderStatus.DELIVERED_COMPLETED);
    }

    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED_COMPLETED;
    }

    public boolean requiresShipment() {
        return items.stream().anyMatch(OrderItem::isPhysical);
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void transition(OrderStatus from, OrderStatus to) {
        if (status != from) {
            throw new IllegalStateException("invalid transition from " + status + " to " + to);
        }
        this.status = to;
    }

    private void requireNotCompleted() {
        if (isCompleted()) {
            throw new IllegalStateException("a completed order cannot be modified");
        }
    }
}
