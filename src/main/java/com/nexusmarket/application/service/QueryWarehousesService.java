package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.port.inbound.QueryWarehousesUseCase;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryWarehousesService implements QueryWarehousesUseCase {

    private final AccessPolicy accessPolicy;
    private final WarehouseRepository warehouses;
    private final SellerRepository sellers;

    public QueryWarehousesService(AccessPolicy accessPolicy, WarehouseRepository warehouses, SellerRepository sellers) {
        this.accessPolicy = accessPolicy;
        this.warehouses = warehouses;
        this.sellers = sellers;
    }

    @Override
    public Warehouse get(User actor, String warehouseId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR, Role.LOGISTICS_OPERATOR, Role.SELLER);
        Warehouse warehouse = warehouses.findById(warehouseId).orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));
        if (actor.getRole() == Role.SELLER && !owns(actor, warehouse)) {
            throw new AccessDeniedException("RG-03: el vendedor no puede consultar bodegas de otros vendedores");
        }
        return warehouse;
    }

    @Override
    public List<Warehouse> list(User actor) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR, Role.LOGISTICS_OPERATOR, Role.SELLER);
        if (actor.getRole() != Role.SELLER) {
            return warehouses.findAll();
        }
        return warehouses.findBySellerId(sellerOf(actor).getId());
    }

    private boolean owns(User actor, Warehouse warehouse) {
        return warehouse.getSellerId() != null && warehouse.getSellerId().equals(sellerOf(actor).getId());
    }

    private Seller sellerOf(User actor) {
        return sellers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
    }
}
