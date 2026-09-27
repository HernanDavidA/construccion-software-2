package com.nexusmarket.application.inventory.port.in;

import com.nexusmarket.domain.inventory.Inventory;

public interface OpenInventoryUseCase {

    Inventory execute(String actorId, String inventoryId, String productId, String warehouseId, int initialQuantity);
}
