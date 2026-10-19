package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ShipmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShipmentMongoRepository extends MongoRepository<ShipmentDocument, String> {
    Optional<ShipmentDocument> findByOrderId(String orderId);
}
