package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Cart;

import java.util.Optional;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findByBuyerId(String buyerId);
}
