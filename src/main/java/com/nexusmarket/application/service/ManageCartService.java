package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.AddCartItem;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.model.CartItem;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ManageCartUseCase;
import com.nexusmarket.domain.port.outbound.CartRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ManageCartService implements ManageCartUseCase {

    private final AccessPolicy accessPolicy;
    private final CartRepository carts;
    private final ProductRepository products;

    public ManageCartService(AccessPolicy accessPolicy, CartRepository carts, ProductRepository products) {
        this.accessPolicy = accessPolicy;
        this.carts = carts;
        this.products = products;
    }

    @Override
    public Cart view(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER);
        return loadOrCreate(buyerOf(actor));
    }

    @Override
    public Cart addItem(User actor, AddCartItem command) {
        accessPolicy.requireRole(actor, Role.BUYER);
        Buyer buyer = buyerOf(actor);
        if (buyer.getCommercialStatus() != BuyerCommercialStatus.ENABLED) {
            throw new BusinessRuleException("El comprador no está habilitado para comprar");
        }
        Product product = products.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (product.getStatus() != ProductStatus.PUBLISHED) {
            throw new BusinessRuleException("Solo se pueden agregar productos publicados");
        }
        product.requireVariant(command.variantId());
        Cart cart = loadOrCreate(buyer);
        cart.add(new CartItem(product.getId(), command.variantId(), command.quantity()));
        return carts.save(cart);
    }

    @Override
    public Cart removeItem(User actor, String productId, String variantId) {
        accessPolicy.requireRole(actor, Role.BUYER);
        Cart cart = loadOrCreate(buyerOf(actor));
        cart.remove(productId, variantId);
        return carts.save(cart);
    }

    @Override
    public Cart clear(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER);
        Cart cart = loadOrCreate(buyerOf(actor));
        cart.clear();
        return carts.save(cart);
    }

    private Cart loadOrCreate(Buyer buyer) {
        return carts.findByBuyerId(buyer.getId())
                .orElseGet(() -> new Cart(UUID.randomUUID().toString(), buyer.getId(), List.of()));
    }

    private Buyer buyerOf(User actor) {
        if (actor instanceof Buyer buyer) {
            return buyer;
        }
        throw new ResourceNotFoundException("Comprador no encontrado");
    }
}
