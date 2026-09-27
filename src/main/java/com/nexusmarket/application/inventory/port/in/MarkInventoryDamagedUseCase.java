package com.nexusmarket.application.inventory.port.in;

import com.nexusmarket.domain.inventory.Inventory;

public interface MarkInventoryDamagedUseCase {

    Inventory execute(String actorId, String inventoryId);
}
