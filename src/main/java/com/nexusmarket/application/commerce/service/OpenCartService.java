package com.nexusmarket.application.commerce.service;

import com.nexusmarket.application.commerce.port.in.OpenCartUseCase;
import com.nexusmarket.application.commerce.port.out.CartRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.commerce.Cart;
import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Opens a cart for a buyer who can purchase. Reuses the existing open cart (OBJ-07).
 */
public class OpenCartService implements OpenCartUseCase {

    private final CartRepository carts;
    private final UserRepository users;

    public OpenCartService(CartRepository carts, UserRepository users) {
        this.carts = Objects.requireNonNull(carts);
        this.users = Objects.requireNonNull(users);
    }

    public Cart execute(String buyerId) {
        User user = users.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("buyer not found"));
        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException("user is not a buyer");
        }
        if (!buyer.canBuy()) {
            throw new IllegalStateException("the buyer is not enabled to purchase");
        }
        return carts.findOpenByBuyerId(buyerId).orElseGet(() -> {
            Cart cart = Cart.newFor(buyer);
            carts.save(cart);
            return cart;
        });
    }
}
