package com.nexusmarket.application.inventory.port.in;

import com.nexusmarket.domain.inventory.Inventory;

public interface ReserveStockUseCase {

    Inventory execute(String actorId, String inventoryId, int units);
}
