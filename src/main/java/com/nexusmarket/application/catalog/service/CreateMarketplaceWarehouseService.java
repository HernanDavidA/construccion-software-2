package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.CreateMarketplaceWarehouseUseCase;
import com.nexusmarket.application.catalog.port.out.WarehouseRepository;
import com.nexusmarket.domain.catalog.Warehouse;

import java.util.Objects;

/**
 * Creates a marketplace-owned warehouse (OBJ-04).
 */
public class CreateMarketplaceWarehouseService implements CreateMarketplaceWarehouseUseCase {

    private final WarehouseRepository warehouses;

    public CreateMarketplaceWarehouseService(WarehouseRepository warehouses) {
        this.warehouses = Objects.requireNonNull(warehouses);
    }

    public Warehouse execute(String id, String name, String location) {
        Warehouse warehouse = Warehouse.ofMarketplace(id, name, location);
        warehouses.save(warehouse);
        return warehouse;
    }
}
