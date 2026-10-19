package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.InventoryCondition;

public record AdjustStock(String inventoryId, int delta, InventoryCondition condition) {
}
