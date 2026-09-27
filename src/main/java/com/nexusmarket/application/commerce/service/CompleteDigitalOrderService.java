package com.nexusmarket.application.commerce.service;

import com.nexusmarket.application.commerce.port.in.CompleteDigitalOrderUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.domain.commerce.Order;
import com.nexusmarket.domain.commerce.OrderStatus;

import java.util.Objects;

/**
 * Completes a paid digital-only order without a shipment (OBJ-08).
 */
public class CompleteDigitalOrderService implements CompleteDigitalOrderUseCase {

    private final OrderRepository orders;

    public CompleteDigitalOrderService(OrderRepository orders) {
        this.orders = Objects.requireNonNull(orders);
    }

    public Order execute(String orderId) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("digital delivery requires a paid order");
        }
        order.completeDigitalDelivery();
        orders.save(order);
        return order;
    }
}
