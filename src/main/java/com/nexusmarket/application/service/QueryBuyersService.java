package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryBuyersUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryBuyersService implements QueryBuyersUseCase {

    private final AccessPolicy accessPolicy;
    private final BuyerRepository buyers;

    public QueryBuyersService(AccessPolicy accessPolicy, BuyerRepository buyers) {
        this.accessPolicy = accessPolicy;
        this.buyers = buyers;
    }

    @Override
    public Buyer getOwn(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER);
        return own(actor);
    }

    @Override
    public Buyer get(User actor, String buyerId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Buyer buyer = buyers.findById(buyerId).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
        if (actor.getRole() == Role.BUYER && !buyer.getId().equals(actor.getId())) {
            throw new AccessDeniedException("El comprador no puede administrar información de otros compradores");
        }
        return buyer;
    }

    @Override
    public List<Buyer> list(User actor) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR);
        return buyers.findAll();
    }

    private Buyer own(User actor) {
        if (actor instanceof Buyer buyer) {
            return buyer;
        }
        throw new ResourceNotFoundException("Comprador no encontrado");
    }
}
