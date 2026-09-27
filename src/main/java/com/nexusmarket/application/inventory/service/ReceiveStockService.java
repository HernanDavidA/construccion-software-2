package com.nexusmarket.application.inventory.service;

import com.nexusmarket.application.inventory.port.in.ReceiveStockUseCase;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.inventory.StockStatus;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Receives units into an existing inventory (OBJ-06).
 */
public class ReceiveStockService implements ReceiveStockUseCase {

    private final InventoryRepository inventories;
    private final UserRepository users;

    public ReceiveStockService(InventoryRepository inventories, UserRepository users) {
        this.inventories = Objects.requireNonNull(inventories);
        this.users = Objects.requireNonNull(users);
    }

    public Inventory execute(String actorId, String inventoryId, int units) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("actor not found"));
        if (!actor.isActive()) {
            throw new IllegalStateException("the actor is not active");
        }
        if (actor instanceof Seller seller) {
            if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
                throw new IllegalStateException("a suspended seller cannot receive stock");
            }
        } else if (!(actor instanceof LogisticsOperator)) {
            throw new IllegalStateException("only a seller or logistics operator can receive stock");
        }
        Inventory inventory = inventories.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("inventory not found"));
        if (inventory.getProduct().getSeller().getSellerStatus() != SellerStatus.ACTIVE) {
            throw new IllegalStateException("a suspended seller cannot receive stock");
        }
        if (inventory.getStatus() == StockStatus.DAMAGED) {
            throw new IllegalStateException("damaged inventory cannot receive operational stock");
        }
        inventory.receive(units);
        inventories.save(inventory);
        return inventory;
    }
}
