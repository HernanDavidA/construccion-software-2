package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryOrdersUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryOrdersService implements QueryOrdersUseCase {

    private final AccessPolicy accessPolicy;
    private final OrderRepository orders;
    private final BuyerRepository buyers;
    private final SellerRepository sellers;
    private final ProductRepository products;

    public QueryOrdersService(
            AccessPolicy accessPolicy,
            OrderRepository orders,
            BuyerRepository buyers,
            SellerRepository sellers,
            ProductRepository products) {
        this.accessPolicy = accessPolicy;
        this.orders = orders;
        this.buyers = buyers;
        this.sellers = sellers;
        this.products = products;
    }

    @Override
    public Order get(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.SELLER, Role.LOGISTICS_OPERATOR, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Order order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        requireVisible(actor, order);
        return order;
    }

    @Override
    public List<Order> list(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.SELLER, Role.LOGISTICS_OPERATOR, Role.ADMINISTRATOR, Role.SUPERVISOR);
        if (actor.getRole() == Role.BUYER) {
            return orders.findByBuyerId(buyerOf(actor).getId());
        }
        if (actor.getRole() == Role.SELLER) {
            String sellerId = sellerOf(actor).getId();
            return orders.findAll().stream().filter(order -> containsSellerProduct(order, sellerId)).toList();
        }
        return orders.findAll();
    }

    private void requireVisible(User actor, Order order) {
        if (actor.getRole() == Role.BUYER && !buyerOf(actor).getId().equals(order.getBuyerId())) {
            throw new AccessDeniedException("El comprador no puede administrar pedidos de otros compradores");
        }
        if (actor.getRole() == Role.SELLER && !containsSellerProduct(order, sellerOf(actor).getId())) {
            throw new AccessDeniedException("RG-03: el pedido no contiene productos del vendedor");
        }
    }

    private boolean containsSellerProduct(Order order, String sellerId) {
        return order.getLines().stream().anyMatch(line -> products.findById(line.productId())
                .map(product -> product.belongsTo(sellerId))
                .orElse(false));
    }

    private Buyer buyerOf(User actor) {
        return buyers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
    }

    private Seller sellerOf(User actor) {
        return sellers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
    }
}
