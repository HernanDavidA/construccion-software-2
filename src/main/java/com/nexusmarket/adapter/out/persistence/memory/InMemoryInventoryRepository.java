package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.domain.inventory.Inventory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<String, Inventory> inventories = new LinkedHashMap<>();

    @Override
    public void save(Inventory inventory) {
        inventories.put(inventory.getId(), inventory);
    }

    @Override
    public Optional<Inventory> findById(String id) {
        return Optional.ofNullable(inventories.get(id));
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId) {
        return inventories.values().stream()
                .filter(inventory -> inventory.getProduct().getId().equals(productId)
                        && inventory.getWarehouse().getId().equals(warehouseId))
                .findFirst();
    }

    @Override
    public List<Inventory> findByProductId(String productId) {
        return inventories.values().stream()
                .filter(inventory -> inventory.getProduct().getId().equals(productId))
                .toList();
    }
}
