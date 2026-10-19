package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryCondition;
import com.nexusmarket.domain.model.User;

public interface MarkInventoryConditionUseCase {
    Inventory mark(User actor, String inventoryId, InventoryCondition condition);
}
