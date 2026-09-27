package com.nexusmarket.application.inventory.port.in;

import com.nexusmarket.domain.inventory.Inventory;

public interface ReceiveStockUseCase {

    Inventory execute(String actorId, String inventoryId, int units);
}
