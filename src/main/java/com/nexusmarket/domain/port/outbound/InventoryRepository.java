package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(String id);

    Optional<Inventory> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId);

    Optional<Inventory> findWithAvailability(String productId, String variantId, int minimumQuantity);

    List<Inventory> findAll();

    InventoryMovement saveMovement(InventoryMovement movement);

    List<InventoryMovement> findMovements(String inventoryId);
}
