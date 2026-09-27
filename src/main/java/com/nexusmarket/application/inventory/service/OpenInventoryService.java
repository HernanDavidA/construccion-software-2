package com.nexusmarket.application.inventory.service;

import com.nexusmarket.application.inventory.port.in.OpenInventoryUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.application.catalog.port.out.WarehouseRepository;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.Warehouse;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Opens stock for a physical product in a warehouse. The product+warehouse pair is unique (OBJ-06).
 */
public class OpenInventoryService implements OpenInventoryUseCase {

    private final InventoryRepository inventories;
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final UserRepository users;

    public OpenInventoryService(InventoryRepository inventories, ProductRepository products,
                                WarehouseRepository warehouses, UserRepository users) {
        this.inventories = Objects.requireNonNull(inventories);
        this.products = Objects.requireNonNull(products);
        this.warehouses = Objects.requireNonNull(warehouses);
        this.users = Objects.requireNonNull(users);
    }

    public Inventory execute(String actorId, String inventoryId, String productId,
                             String warehouseId, int initialQuantity) {
        requireStockActor(actorId);
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        Warehouse warehouse = warehouses.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("warehouse not found"));
        if (inventories.findByProductAndWarehouse(productId, warehouseId).isPresent()) {
            throw new IllegalStateException("inventory already exists for this product and warehouse");
        }
        Inventory inventory = new Inventory(inventoryId, product, warehouse, initialQuantity);
        inventories.save(inventory);
        return inventory;
    }

    private void requireStockActor(String actorId) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("actor not found"));
        if (!(actor instanceof Seller) && !(actor instanceof LogisticsOperator)) {
            throw new IllegalStateException("only a seller or logistics operator can manage inventory");
        }
        if (!actor.isActive()) {
            throw new IllegalStateException("the actor is not active");
        }
    }
}
