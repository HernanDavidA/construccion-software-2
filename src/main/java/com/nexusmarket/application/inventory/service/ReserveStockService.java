package com.nexusmarket.application.inventory.service;

import com.nexusmarket.application.inventory.port.in.ReserveStockUseCase;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Reserves available units. Damaged or insufficient stock is rejected by the entity (OBJ-06).
 */
public class ReserveStockService implements ReserveStockUseCase {

    private final InventoryRepository inventories;
    private final UserRepository users;

    public ReserveStockService(InventoryRepository inventories, UserRepository users) {
        this.inventories = Objects.requireNonNull(inventories);
        this.users = Objects.requireNonNull(users);
    }

    public Inventory execute(String actorId, String inventoryId, int units) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("actor not found"));
        if (!(actor instanceof Seller) && !(actor instanceof LogisticsOperator)) {
            throw new IllegalStateException("only a seller or logistics operator can reserve stock");
        }
        if (!actor.isActive()) {
            throw new IllegalStateException("the actor is not active");
        }
        Inventory inventory = inventories.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("inventory not found"));
        inventory.reserve(units);
        inventories.save(inventory);
        return inventory;
    }
}
