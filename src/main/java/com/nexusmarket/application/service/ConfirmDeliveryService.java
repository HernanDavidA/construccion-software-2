package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ConfirmDeliveryUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ConfirmDeliveryService implements ConfirmDeliveryUseCase {

    private final AccessPolicy accessPolicy;
    private final OrderRepository orders;
    private final ShipmentRepository shipments;
    private final BuyerRepository buyers;

    public ConfirmDeliveryService(
            AccessPolicy accessPolicy,
            OrderRepository orders,
            ShipmentRepository shipments,
            BuyerRepository buyers) {
        this.accessPolicy = accessPolicy;
        this.orders = orders;
        this.shipments = shipments;
        this.buyers = buyers;
    }

    @Override
    public Order confirm(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.LOGISTICS_OPERATOR);
        Order order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (actor.getRole() == Role.BUYER) {
            Buyer buyer = buyers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
            if (!buyer.getId().equals(order.getBuyerId())) {
                throw new AccessDeniedException("El comprador no puede administrar pedidos de otros compradores");
            }
        }
        if (!order.containsPhysicalProduct()) {
            throw new BusinessRuleException("El producto digital se entrega al confirmar el pago");
        }
        Shipment shipment = shipments.findByOrderId(order.getId()).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        shipment.deliver();
        order.advanceTo(OrderStatus.COMPLETED);
        shipments.save(shipment);
        return orders.save(order);
    }
}
