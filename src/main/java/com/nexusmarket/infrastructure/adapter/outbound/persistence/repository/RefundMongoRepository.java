package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.RefundDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefundMongoRepository extends MongoRepository<RefundDocument, String> {
    Optional<RefundDocument> findByReturnId(String returnId);
}
