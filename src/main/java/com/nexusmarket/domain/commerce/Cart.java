package com.nexusmarket.domain.commerce;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductVariant;
import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Provisional product selection before confirming the order (OBJ-07).
 */
public class Cart {

    private final String id;
    private final Buyer buyer;
    private Address deliveryAddress;
    private final List<CartItem> items;

    public Cart(String id, Buyer buyer, Address deliveryAddress) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        this.id = id.trim();
        this.buyer = Objects.requireNonNull(buyer, "buyer is required");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress is required");
        this.items = new ArrayList<>();
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

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress is required");
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Product product, ProductVariant variant, int quantity) {
        if (!buyer.canBuy()) {
            throw new IllegalStateException("the buyer is not enabled to purchase");
        }
        items.add(new CartItem(product, variant, quantity));
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Order confirmOrder(String orderId) {
        if (isEmpty()) {
            throw new IllegalStateException("an empty cart cannot be confirmed");
        }
        if (!buyer.canBuy()) {
            throw new IllegalStateException("the buyer is not enabled to purchase");
        }
        Order order = new Order(orderId, buyer, deliveryAddress);
        for (CartItem item : items) {
            order.addItem(item.getProduct(), item.getVariant(), item.getQuantity());
        }
        order.confirm();
        clear();
        return order;
    }

    public static Cart newFor(Buyer buyer) {
        return new Cart(UUID.randomUUID().toString(), buyer, buyer.getPrimaryAddress());
    }
}
