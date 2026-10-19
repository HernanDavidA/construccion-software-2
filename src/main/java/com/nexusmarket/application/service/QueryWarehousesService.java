package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.inbound.QueryWarehousesUseCase;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryWarehousesService implements QueryWarehousesUseCase {

    private final AccessPolicy accessPolicy;
    private final WarehouseRepository warehouses;

    public QueryWarehousesService(AccessPolicy accessPolicy, WarehouseRepository warehouses) {
        this.accessPolicy = accessPolicy;
        this.warehouses = warehouses;
    }

    @Override
    public Warehouse get(User actor, String warehouseId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR, Role.LOGISTICS_OPERATOR, Role.SELLER);
        Warehouse warehouse = warehouses.findById(warehouseId).orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));
        if (actor.getRole() == Role.SELLER && !visibleToSeller(actor, warehouse)) {
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
        String sellerId = sellerOf(actor).getId();
        return warehouses.findAll().stream()
                .filter(warehouse -> warehouse.getType() == WarehouseType.MARKETPLACE || sellerId.equals(warehouse.getSellerId()))
                .toList();
    }

    private boolean visibleToSeller(User actor, Warehouse warehouse) {
        return warehouse.getType() == WarehouseType.MARKETPLACE || owns(actor, warehouse);
    }

    private boolean owns(User actor, Warehouse warehouse) {
        return warehouse.getSellerId() != null && warehouse.getSellerId().equals(sellerOf(actor).getId());
    }

    private Seller sellerOf(User actor) {
        if (actor instanceof Seller seller) {
            return seller;
        }
        throw new ResourceNotFoundException("Vendedor no encontrado");
    }
}
