package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryShipmentUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class QueryShipmentService implements QueryShipmentUseCase {

    private final AccessPolicy accessPolicy;
    private final ShipmentRepository shipments;
    private final OrderRepository orders;
    private final BuyerRepository buyers;

    public QueryShipmentService(
            AccessPolicy accessPolicy,
            ShipmentRepository shipments,
            OrderRepository orders,
            BuyerRepository buyers) {
        this.accessPolicy = accessPolicy;
        this.shipments = shipments;
        this.orders = orders;
        this.buyers = buyers;
    }

    @Override
    public Shipment getByOrder(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.LOGISTICS_OPERATOR, Role.BUYER, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Shipment shipment = shipments.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        if (actor.getRole() == Role.BUYER) {
            Order order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
            Buyer buyer = buyers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
            if (!buyer.getId().equals(order.getBuyerId())) {
                throw new AccessDeniedException("El comprador no puede consultar envíos de otros compradores");
            }
        }
        return shipment;
    }
}
