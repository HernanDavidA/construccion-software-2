package com.nexusmarket.domain.inventory;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.Warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Stock of a product in a specific warehouse (OBJ-06).
 * Does not allow negative quantities or reservation of damaged or nonexistent stock.
 */
public class Inventory {

    private final String id;
    private final Product product;
    private final Warehouse warehouse;
    private int quantity;
    private StockStatus status;
    private final List<InventoryMovement> movements;

    public Inventory(String id, Product product, Warehouse warehouse, int initialQuantity) {
        this.product = Objects.requireNonNull(product, "product is required");
        this.warehouse = Objects.requireNonNull(warehouse, "warehouse is required");
        if (!product.isPhysical()) {
            throw new IllegalArgumentException("only physical products manage inventory");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("negative stock is not allowed");
        }
        this.id = requireText(id, "id");
        this.quantity = initialQuantity;
        this.status = StockStatus.AVAILABLE;
        this.movements = new ArrayList<>();
        if (initialQuantity > 0) {
            record(InventoryMovementType.RECEIPT, initialQuantity, "initial stock");
        }
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public int getQuantity() {
        return quantity;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void markDamaged() {
        this.status = StockStatus.DAMAGED;
    }

    public void markAvailable() {
        this.status = StockStatus.AVAILABLE;
    }

    public List<InventoryMovement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    public void receive(int units) {
        requireUnits(units);
        quantity += units;
        record(InventoryMovementType.RECEIPT, units, "receipt");
    }

    public void reserve(int units) {
        requireUnits(units);
        if (status == StockStatus.DAMAGED) {
            throw new IllegalStateException("damaged inventory cannot be reserved");
        }
        if (quantity < units) {
            throw new IllegalStateException("nonexistent inventory cannot be reserved");
        }
        quantity -= units;
        record(InventoryMovementType.RESERVATION, units, "reservation");
    }

    public void issueForSale(int units) {
        requireUnits(units);
        if (quantity < units) {
            throw new IllegalStateException("negative stock is not allowed");
        }
        quantity -= units;
        record(InventoryMovementType.SALE_ISSUE, units, "sale issue");
    }

    public void adjust(int resultingQuantity) {
        if (resultingQuantity < 0) {
            throw new IllegalArgumentException("negative stock is not allowed");
        }
        int difference = Math.abs(resultingQuantity - quantity);
        if (difference == 0) {
            return;
        }
        quantity = resultingQuantity;
        record(InventoryMovementType.ADJUSTMENT, difference, "stock adjustment");
    }

    public void returnStock(int units) {
        requireUnits(units);
        quantity += units;
        if (status == StockStatus.RESERVED && quantity > 0) {
            status = StockStatus.AVAILABLE;
        }
        record(InventoryMovementType.RETURN, units, "return to warehouse");
    }

    private void record(InventoryMovementType type, int units, String note) {
        movements.add(new InventoryMovement(UUID.randomUUID().toString(), type, units, note));
    }

    private static void requireUnits(int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("units must be greater than zero");
        }
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }
}
