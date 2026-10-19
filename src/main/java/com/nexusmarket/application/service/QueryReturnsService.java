package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryReturnsUseCase;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ReturnRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryReturnsService implements QueryReturnsUseCase {

    private final AccessPolicy accessPolicy;
    private final ReturnRepository returns;
    private final OrderRepository orders;

    public QueryReturnsService(AccessPolicy accessPolicy, ReturnRepository returns, OrderRepository orders) {
        this.accessPolicy = accessPolicy;
        this.returns = returns;
        this.orders = orders;
    }

    @Override
    public List<ReturnRequest> listByOrder(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Order order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (actor.getRole() == Role.BUYER) {
            if (!(actor instanceof Buyer) || !actor.getId().equals(order.getBuyerId())) {
                throw new AccessDeniedException("El comprador no puede consultar devoluciones de otros compradores");
            }
        }
        return returns.findByOrderId(orderId);
    }
}
