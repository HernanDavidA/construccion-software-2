package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.VariantIds;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InventoryDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InventoryMovementDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.InventoryMongoRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.InventoryMovementMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryMongoRepository inventory;
    private final InventoryMovementMongoRepository movements;

    public InventoryRepositoryAdapter(InventoryMongoRepository inventory, InventoryMovementMongoRepository movements) {
        this.inventory = inventory;
        this.movements = movements;
    }

    @Override
    public Inventory save(Inventory item) {
        return inventory.save(InventoryDocument.from(item)).toDomain();
    }

    @Override
    public Optional<Inventory> findById(String id) {
        return inventory.findById(id).map(InventoryDocument::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId) {
        return inventory.findByProductId(productId).stream()
                .map(InventoryDocument::toDomain)
                .filter(item -> item.matches(productId, variantId, warehouseId))
                .findFirst();
    }

    @Override
    public Optional<Inventory> findWithAvailability(String productId, String variantId, int minimumQuantity) {
        return inventory.findByProductId(productId).stream()
                .map(InventoryDocument::toDomain)
                .filter(item -> VariantIds.same(item.getVariantId(), variantId))
                .filter(item -> item.covers(minimumQuantity))
                .findFirst();
    }

    @Override
    public List<Inventory> findAll() {
        return inventory.findAll().stream().map(InventoryDocument::toDomain).toList();
    }

    @Override
    public InventoryMovement saveMovement(InventoryMovement movement) {
        return movements.save(InventoryMovementDocument.from(movement)).toDomain();
    }

    @Override
    public List<InventoryMovement> findMovements(String inventoryId) {
        return movements.findByInventoryIdOrderByOccurredAtAsc(inventoryId).stream().map(InventoryMovementDocument::toDomain).toList();
    }
}
