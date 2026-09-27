package com.nexusmarket.application.inventory.service;

import com.nexusmarket.application.inventory.port.in.MarkInventoryDamagedUseCase;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Marks inventory as damaged so it cannot be reserved (OBJ-06).
 */
public class MarkInventoryDamagedService implements MarkInventoryDamagedUseCase {

    private final InventoryRepository inventories;
    private final UserRepository users;

    public MarkInventoryDamagedService(InventoryRepository inventories, UserRepository users) {
        this.inventories = Objects.requireNonNull(inventories);
        this.users = Objects.requireNonNull(users);
    }

    public Inventory execute(String actorId, String inventoryId) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("actor not found"));
        if (!(actor instanceof Seller) && !(actor instanceof LogisticsOperator)) {
            throw new IllegalStateException("only a seller or logistics operator can mark inventory damaged");
        }
        if (!actor.isActive()) {
            throw new IllegalStateException("the actor is not active");
        }
        Inventory inventory = inventories.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("inventory not found"));
        inventory.markDamaged();
        inventories.save(inventory);
        return inventory;
    }
}
