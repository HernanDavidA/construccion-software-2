package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.WarehouseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WarehouseMongoRepository extends MongoRepository<WarehouseDocument, String> {
    List<WarehouseDocument> findBySellerId(String sellerId);
}
