package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.domain.commerce.Order;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orders = new LinkedHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
}
