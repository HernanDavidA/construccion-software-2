package com.nexusmarket.domain.model;

import java.time.Instant;

public record InventoryMovement(
        String id,
        String inventoryId,
        InventoryMovementType type,
        int quantity,
        Instant occurredAt,
        String reference) {
}
