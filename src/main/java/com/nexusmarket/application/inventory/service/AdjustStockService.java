package com.nexusmarket.application.inventory.service;

import com.nexusmarket.application.inventory.port.in.AdjustStockUseCase;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Adjusts on-hand quantity to an absolute non-negative value (OBJ-06).
 */
public class AdjustStockService implements AdjustStockUseCase {

    private final InventoryRepository inventories;
    private final UserRepository users;

    public AdjustStockService(InventoryRepository inventories, UserRepository users) {
        this.inventories = Objects.requireNonNull(inventories);
        this.users = Objects.requireNonNull(users);
    }

    public Inventory execute(String actorId, String inventoryId, int resultingQuantity) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("actor not found"));
        if (!(actor instanceof Seller) && !(actor instanceof LogisticsOperator)) {
            throw new IllegalStateException("only a seller or logistics operator can adjust stock");
        }
        if (!actor.isActive()) {
            throw new IllegalStateException("the actor is not active");
        }
        Inventory inventory = inventories.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("inventory not found"));
        inventory.adjust(resultingQuantity);
        inventories.save(inventory);
        return inventory;
    }
}
