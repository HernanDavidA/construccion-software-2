package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.OrderDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.OrderMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderMongoRepository mongo;

    public OrderRepositoryAdapter(OrderMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Order save(Order order) {
        return mongo.save(OrderDocument.from(order)).toDomain();
    }

    @Override
    public Optional<Order> findById(String id) {
        return mongo.findById(id).map(OrderDocument::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return mongo.findAll().stream().map(OrderDocument::toDomain).toList();
    }

    @Override
    public List<Order> findByBuyerId(String buyerId) {
        return mongo.findByBuyerId(buyerId).stream().map(OrderDocument::toDomain).toList();
    }
}
