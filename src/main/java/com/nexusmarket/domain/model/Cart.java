package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final String id;
    private final String buyerId;
    private final List<CartItem> items = new ArrayList<>();

    public Cart(String id, String buyerId, List<CartItem> items) {
        this.id = RequiredText.require(id, "El identificador del carrito es obligatorio");
        this.buyerId = RequiredText.require(buyerId, "El carrito debe pertenecer a un comprador");
        if (items != null) {
            items.forEach(this::add);
        }
    }

    public void add(CartItem item) {
        if (item == null || item.productId() == null || item.productId().isBlank()) {
            throw new BusinessRuleException("El producto del carrito es obligatorio");
        }
        if (item.quantity() <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor que cero");
        }
        for (int index = 0; index < items.size(); index++) {
            CartItem current = items.get(index);
            if (current.sameProduct(item)) {
                items.set(index, current.plus(item.quantity()));
                return;
            }
        }
        items.add(new CartItem(item.productId(), item.normalizedVariantId(), item.quantity()));
    }

    public void remove(String productId, String variantId) {
        boolean removed = items.removeIf(item -> item.productId().equals(productId) && VariantIds.same(item.variantId(), variantId));
        if (!removed) {
            throw new BusinessRuleException("El producto no está en el carrito");
        }
    }

    public void clear() {
        items.clear();
    }

    public String getId() {
        return id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
