package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.port.outbound.CartRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.CartDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.CartMongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CartRepositoryAdapter implements CartRepository {

    private final CartMongoRepository mongo;

    public CartRepositoryAdapter(CartMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Cart save(Cart cart) {
        return mongo.save(CartDocument.from(cart)).toDomain();
    }

    @Override
    public Optional<Cart> findByBuyerId(String buyerId) {
        return mongo.findByBuyerId(buyerId).map(CartDocument::toDomain);
    }
}
