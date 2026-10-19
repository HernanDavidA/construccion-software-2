package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QuerySellersUseCase;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuerySellersService implements QuerySellersUseCase {

    private final AccessPolicy accessPolicy;
    private final SellerRepository sellers;

    public QuerySellersService(AccessPolicy accessPolicy, SellerRepository sellers) {
        this.accessPolicy = accessPolicy;
        this.sellers = sellers;
    }

    @Override
    public Seller get(User actor, String sellerId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR, Role.SELLER);
        Seller seller = sellers.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        if (actor.getRole() == Role.SELLER) {
            Seller own = sellers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
            if (!own.getId().equals(seller.getId())) {
                throw new AccessDeniedException("RG-03: el vendedor no puede administrar información de otros vendedores");
            }
        }
        return seller;
    }

    @Override
    public List<Seller> list(User actor) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR);
        return sellers.findAll();
    }
}
