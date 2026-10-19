package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String id);

    List<Order> findAll();

    List<Order> findByBuyerId(String buyerId);
}
