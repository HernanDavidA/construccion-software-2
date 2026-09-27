package com.nexusmarket.application.commerce.port.out;

import com.nexusmarket.domain.commerce.Cart;

import java.util.Optional;

public interface CartRepository {

    void save(Cart cart);

    Optional<Cart> findById(String id);

    Optional<Cart> findOpenByBuyerId(String buyerId);
}
