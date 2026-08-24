package com.nexusmarket.domain.inventory;

/**
 * Types of movement over distributed stock.
 */
public enum InventoryMovementType {
    RECEIPT,
    RESERVATION,
    SALE_ISSUE,
    ADJUSTMENT,
    RETURN
}
