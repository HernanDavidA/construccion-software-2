package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory")
public class InventoryDocument {

    @Id
    private String id;
    private String productId;
    private String variantId;
    private String warehouseId;
    private int availableQuantity;
    private int reservedQuantity;
    private InventoryCondition condition;

    public static InventoryDocument from(Inventory inventory) {
        return new InventoryDocument(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getVariantId(),
                inventory.getWarehouseId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getCondition());
    }

    public Inventory toDomain() {
        return new Inventory(id, productId, variantId, warehouseId, availableQuantity, reservedQuantity, condition);
    }
}
