package com.nexusmarket.application.logistics.service;

import com.nexusmarket.application.logistics.port.in.CreateShipmentUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.logistics.port.out.ShipmentRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.commerce.Order;
import com.nexusmarket.domain.commerce.OrderStatus;
import com.nexusmarket.domain.logistics.Shipment;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Creates a shipment for a paid order that requires physical dispatch (OBJ-10).
 */
public class CreateShipmentService implements CreateShipmentUseCase {

    private final ShipmentRepository shipments;
    private final OrderRepository orders;
    private final UserRepository users;

    public CreateShipmentService(ShipmentRepository shipments, OrderRepository orders,
                                 UserRepository users) {
        this.shipments = Objects.requireNonNull(shipments);
        this.orders = Objects.requireNonNull(orders);
        this.users = Objects.requireNonNull(users);
    }

    public Shipment execute(String actorId, String shipmentId, String orderId) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("operator not found"));
        if (!(actor instanceof LogisticsOperator operator)) {
            throw new IllegalStateException("only a logistics operator can create a shipment");
        }
        if (!operator.isActive()) {
            throw new IllegalStateException("the operator is not active");
        }
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("shipment requires a paid order");
        }
        Shipment shipment = new Shipment(shipmentId, order, operator);
        shipments.save(shipment);
        orders.save(order);
        return shipment;
    }
}
