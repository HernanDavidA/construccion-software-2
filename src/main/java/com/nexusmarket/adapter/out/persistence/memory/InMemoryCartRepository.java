package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.commerce.port.out.CartRepository;
import com.nexusmarket.domain.commerce.Cart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCartRepository implements CartRepository {

    private final Map<String, Cart> carts = new LinkedHashMap<>();

    @Override
    public void save(Cart cart) {
        carts.put(cart.getId(), cart);
    }

    @Override
    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }

    @Override
    public Optional<Cart> findOpenByBuyerId(String buyerId) {
        return carts.values().stream()
                .filter(cart -> cart.getBuyer().getId().equals(buyerId))
                .findFirst();
    }
}
