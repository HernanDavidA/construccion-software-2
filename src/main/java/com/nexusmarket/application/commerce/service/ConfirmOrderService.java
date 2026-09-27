package com.nexusmarket.application.commerce.service;

import com.nexusmarket.application.commerce.port.in.ConfirmOrderUseCase;
import com.nexusmarket.application.commerce.port.out.CartRepository;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.domain.commerce.Cart;
import com.nexusmarket.domain.commerce.CartItem;
import com.nexusmarket.domain.commerce.Order;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.inventory.StockStatus;

import java.util.Objects;

/**
 * Confirms the cart into an order and reserves stock of physical lines (OBJ-08, OBJ-06).
 */
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final CartRepository carts;
    private final OrderRepository orders;
    private final InventoryRepository inventories;

    public ConfirmOrderService(CartRepository carts, OrderRepository orders,
                               InventoryRepository inventories) {
        this.carts = Objects.requireNonNull(carts);
        this.orders = Objects.requireNonNull(orders);
        this.inventories = Objects.requireNonNull(inventories);
    }

    public Order execute(String cartId, String orderId) {
        Cart cart = carts.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("cart not found"));
        if (cart.isEmpty()) {
            throw new IllegalStateException("an empty cart cannot be confirmed");
        }
        if (!cart.getBuyer().canBuy()) {
            throw new IllegalStateException("the buyer is not enabled to purchase");
        }
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().isPhysical()) {
                Inventory inventory = requireAvailableStock(item.getProduct().getId(), item.getQuantity());
                inventory.reserve(item.getQuantity());
                inventories.save(inventory);
            }
        }
        Order order = cart.confirmOrder(orderId);
        carts.save(cart);
        orders.save(order);
        return order;
    }

    private Inventory requireAvailableStock(String productId, int quantity) {
        return inventories.findByProductId(productId).stream()
                .filter(inventory -> inventory.getStatus() == StockStatus.AVAILABLE
                        && inventory.getQuantity() >= quantity)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("insufficient available stock"));
    }
}
