package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.DispatchShipmentUseCase;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class DispatchShipmentService implements DispatchShipmentUseCase {

    private final AccessPolicy accessPolicy;
    private final ShipmentRepository shipments;
    private final OrderRepository orders;

    public DispatchShipmentService(AccessPolicy accessPolicy, ShipmentRepository shipments, OrderRepository orders) {
        this.accessPolicy = accessPolicy;
        this.shipments = shipments;
        this.orders = orders;
    }

    @Override
    public Shipment dispatch(User actor, String shipmentId) {
        accessPolicy.requireRole(actor, Role.LOGISTICS_OPERATOR);
        Shipment shipment = shipments.findById(shipmentId).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        shipment.dispatch();
        Order order = orders.findById(shipment.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        order.advanceTo(OrderStatus.SHIPPED);
        orders.save(order);
        return shipments.save(shipment);
    }
}
