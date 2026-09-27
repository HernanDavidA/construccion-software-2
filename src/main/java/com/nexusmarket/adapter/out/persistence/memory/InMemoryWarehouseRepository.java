package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.catalog.port.out.WarehouseRepository;
import com.nexusmarket.domain.catalog.Warehouse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWarehouseRepository implements WarehouseRepository {

    private final Map<String, Warehouse> warehouses = new LinkedHashMap<>();

    @Override
    public void save(Warehouse warehouse) {
        warehouses.put(warehouse.getId(), warehouse);
    }

    @Override
    public Optional<Warehouse> findById(String id) {
        return Optional.ofNullable(warehouses.get(id));
    }
}
