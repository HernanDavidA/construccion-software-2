package com.nexusmarket.application.commerce.service;

import com.nexusmarket.application.commerce.port.in.AddItemToCartUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.application.commerce.port.out.CartRepository;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductVariant;
import com.nexusmarket.domain.commerce.Cart;
import com.nexusmarket.domain.inventory.StockStatus;

import java.util.Objects;

/**
 * Adds a published product to the cart. Physical items require enough available stock (OBJ-07).
 */
public class AddItemToCartService implements AddItemToCartUseCase {

    private final CartRepository carts;
    private final ProductRepository products;
    private final InventoryRepository inventories;

    public AddItemToCartService(CartRepository carts, ProductRepository products,
                                InventoryRepository inventories) {
        this.carts = Objects.requireNonNull(carts);
        this.products = Objects.requireNonNull(products);
        this.inventories = Objects.requireNonNull(inventories);
    }

    public Cart execute(String cartId, String productId, ProductVariant variant, int quantity) {
        Cart cart = carts.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("cart not found"));
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        if (product.isPhysical()) {
            boolean enough = inventories.findByProductId(productId).stream()
                    .anyMatch(inventory -> inventory.getStatus() == StockStatus.AVAILABLE
                            && inventory.getQuantity() >= quantity);
            if (!enough) {
                throw new IllegalStateException("insufficient available stock");
            }
        }
        cart.addItem(product, variant, quantity);
        carts.save(cart);
        return cart;
    }
}
