package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InventoryMovementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryMovementMongoRepository extends MongoRepository<InventoryMovementDocument, String> {
    List<InventoryMovementDocument> findByInventoryIdOrderByOccurredAtAsc(String inventoryId);
}
