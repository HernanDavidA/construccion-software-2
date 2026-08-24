package com.nexusmarket.domain.inventory;

/**
 * Condition of the stock. Damaged inventory cannot be reserved.
 */
public enum StockStatus {
    AVAILABLE,
    DAMAGED,
    RESERVED
}
