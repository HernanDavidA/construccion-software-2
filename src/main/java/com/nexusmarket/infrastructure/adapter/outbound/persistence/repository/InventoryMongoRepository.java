package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InventoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryMongoRepository extends MongoRepository<InventoryDocument, String> {
    List<InventoryDocument> findByProductId(String productId);
}
