package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RequestReturn;
import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.ReturnStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.RequestReturnUseCase;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ReturnRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RequestReturnService implements RequestReturnUseCase {

    private final AccessPolicy accessPolicy;
    private final ReturnRepository returns;
    private final OrderRepository orders;

    public RequestReturnService(AccessPolicy accessPolicy, ReturnRepository returns, OrderRepository orders) {
        this.accessPolicy = accessPolicy;
        this.returns = returns;
        this.orders = orders;
    }

    @Override
    public ReturnRequest request(User actor, RequestReturn command) {
        accessPolicy.requireRole(actor, Role.BUYER);
        Order order = orders.findById(command.orderId()).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (!(actor instanceof Buyer buyer) || !buyer.getId().equals(order.getBuyerId())) {
            throw new AccessDeniedException("El comprador no puede devolver pedidos de otros compradores");
        }
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessRuleException("Solo se puede devolver un pedido finalizado");
        }
        boolean active = returns.findByOrderId(order.getId()).stream().anyMatch(item -> item.getStatus() != ReturnStatus.REJECTED);
        if (active) {
            throw new BusinessRuleException("El pedido ya tiene una devolución en curso");
        }
        return returns.save(new ReturnRequest(
                UUID.randomUUID().toString(),
                order.getId(),
                buyer.getId(),
                command.reason(),
                ReturnStatus.REQUESTED,
                Instant.now()));
    }
}
