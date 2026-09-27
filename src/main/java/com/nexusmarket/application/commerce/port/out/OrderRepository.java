package com.nexusmarket.application.commerce.port.out;

import com.nexusmarket.domain.commerce.Order;

import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String id);
}
