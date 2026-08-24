package com.nexusmarket.domain.inventory;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Record of a stock change (receipt, reservation, issue, adjustment or return).
 */
public class InventoryMovement {

    private final String id;
    private final InventoryMovementType type;
    private final int quantity;
    private final LocalDateTime occurredAt;
    private final String note;

    public InventoryMovement(String id, InventoryMovementType type, int quantity, String note) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("movement quantity must be positive");
        }
        this.id = id.trim();
        this.type = Objects.requireNonNull(type, "type is required");
        this.quantity = quantity;
        this.occurredAt = LocalDateTime.now();
        this.note = note == null ? "" : note.trim();
    }

    public String getId() {
        return id;
    }

    public InventoryMovementType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getNote() {
        return note;
    }
}
