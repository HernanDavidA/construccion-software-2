package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryInventoryUseCase {
    List<Inventory> list(User actor);

    List<InventoryMovement> movements(User actor, String inventoryId);
}
