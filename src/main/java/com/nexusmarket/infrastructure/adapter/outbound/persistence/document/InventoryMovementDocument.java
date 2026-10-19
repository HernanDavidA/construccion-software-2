package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.InventoryMovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_movements")
public class InventoryMovementDocument {

    @Id
    private String id;
    private String inventoryId;
    private InventoryMovementType type;
    private int quantity;
    private Instant occurredAt;
    private String reference;

    public static InventoryMovementDocument from(InventoryMovement movement) {
        return new InventoryMovementDocument(movement.id(), movement.inventoryId(), movement.type(), movement.quantity(), movement.occurredAt(), movement.reference());
    }

    public InventoryMovement toDomain() {
        return new InventoryMovement(id, inventoryId, type, quantity, occurredAt, reference);
    }
}
