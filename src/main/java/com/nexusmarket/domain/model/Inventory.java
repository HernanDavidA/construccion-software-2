package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Inventory {

    private final String id;
    private final String productId;
    private final String variantId;
    private final String warehouseId;
    private int availableQuantity;
    private int reservedQuantity;
    private InventoryCondition condition;

    public Inventory(
            String id,
            String productId,
            String variantId,
            String warehouseId,
            int availableQuantity,
            int reservedQuantity,
            InventoryCondition condition) {
        this.id = RequiredText.require(id, "El identificador del inventario es obligatorio");
        this.productId = RequiredText.require(productId, "El inventario debe estar vinculado a un producto");
        this.warehouseId = RequiredText.require(warehouseId, "El inventario debe estar vinculado a una bodega");
        if (availableQuantity < 0 || reservedQuantity < 0) {
            throw new BusinessRuleException("No se permiten existencias negativas");
        }
        this.variantId = VariantIds.normalize(variantId);
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.condition = condition == null ? InventoryCondition.AVAILABLE : condition;
    }

    public static Inventory create(String productId, String variantId, String warehouseId) {
        return new Inventory(UUID.randomUUID().toString(), productId, variantId, warehouseId, 0, 0, InventoryCondition.AVAILABLE);
    }

    public InventoryMovement receive(int quantity, String reference) {
        requirePositive(quantity);
        availableQuantity += quantity;
        return movement(InventoryMovementType.INBOUND, quantity, reference);
    }

    public InventoryMovement reserve(int quantity, String reference) {
        requirePositive(quantity);
        if (condition == InventoryCondition.DAMAGED) {
            throw new BusinessRuleException("No se puede reservar inventario dañado");
        }
        if (availableQuantity < quantity) {
            throw new BusinessRuleException("No se puede reservar inventario inexistente");
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
        return movement(InventoryMovementType.RESERVATION, quantity, reference);
    }

    public InventoryMovement saleOutbound(int quantity, String reference) {
        requirePositive(quantity);
        if (reservedQuantity < quantity) {
            throw new BusinessRuleException("La salida por venta supera la cantidad reservada");
        }
        reservedQuantity -= quantity;
        return movement(InventoryMovementType.SALE_OUTBOUND, quantity, reference);
    }

    public InventoryMovement adjust(int delta, String reference) {
        if (delta == 0) {
            throw new BusinessRuleException("El ajuste de inventario no puede ser cero");
        }
        if (availableQuantity + delta < 0) {
            throw new BusinessRuleException("No se permiten existencias negativas");
        }
        availableQuantity += delta;
        return movement(InventoryMovementType.ADJUSTMENT, delta, reference);
    }

    public InventoryMovement returnStock(int quantity, String reference) {
        requirePositive(quantity);
        availableQuantity += quantity;
        return movement(InventoryMovementType.RETURN, quantity, reference);
    }

    public void markCondition(InventoryCondition newCondition) {
        if (newCondition == null) {
            throw new BusinessRuleException("La condición del inventario es obligatoria");
        }
        this.condition = newCondition;
    }

    public boolean covers(int quantity) {
        return condition == InventoryCondition.AVAILABLE && availableQuantity >= quantity;
    }

    public boolean matches(String productId, String variantId, String warehouseId) {
        return this.productId.equals(productId)
                && VariantIds.same(this.variantId, variantId)
                && this.warehouseId.equals(warehouseId);
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("La cantidad debe ser mayor que cero");
        }
    }

    private InventoryMovement movement(InventoryMovementType type, int quantity, String reference) {
        return new InventoryMovement(UUID.randomUUID().toString(), id, type, quantity, Instant.now(), reference);
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getVariantId() {
        return variantId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public InventoryCondition getCondition() {
        return condition;
    }
}
