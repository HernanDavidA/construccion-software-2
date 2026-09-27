package com.nexusmarket.application.inventory.port.out;

import com.nexusmarket.domain.inventory.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    void save(Inventory inventory);

    Optional<Inventory> findById(String id);

    Optional<Inventory> findByProductAndWarehouse(String productId, String warehouseId);

    List<Inventory> findByProductId(String productId);
}
