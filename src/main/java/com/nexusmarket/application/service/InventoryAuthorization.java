package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import org.springframework.stereotype.Component;

@Component
public class InventoryAuthorization {

    private final InventoryRepository inventory;
    private final ProductRepository products;
    private final WarehouseRepository warehouses;

    public InventoryAuthorization(InventoryRepository inventory, ProductRepository products, WarehouseRepository warehouses) {
        this.inventory = inventory;
        this.products = products;
        this.warehouses = warehouses;
    }

    public void requireOperation(User actor, Product product, Warehouse warehouse) {
        if (actor.getRole() == Role.LOGISTICS_OPERATOR) {
            return;
        }
        Seller seller = sellerOf(actor);
        if (!product.belongsTo(seller.getId())) {
            throw new AccessDeniedException("RG-03: el vendedor solo administra el inventario de sus productos");
        }
        boolean ownWarehouse = warehouse.getType() == WarehouseType.SELLER && seller.getId().equals(warehouse.getSellerId());
        if (!ownWarehouse && warehouse.getType() != WarehouseType.MARKETPLACE) {
            throw new AccessDeniedException("RG-03: el vendedor no puede operar esta bodega");
        }
    }

    public void requireOnInventory(User actor, Inventory item) {
        if (actor.getRole() == Role.LOGISTICS_OPERATOR) {
            return;
        }
        requireOperation(actor, productOf(item), warehouseOf(item));
    }

    public Product productOf(Inventory item) {
        return products.findById(item.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    public Inventory find(String inventoryId) {
        return inventory.findById(inventoryId).orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));
    }

    public Seller sellerOf(User actor) {
        if (actor instanceof Seller seller) {
            return seller;
        }
        throw new ResourceNotFoundException("Vendedor no encontrado");
    }

    private Warehouse warehouseOf(Inventory item) {
        return warehouses.findById(item.getWarehouseId()).orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));
    }
}
