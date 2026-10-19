package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.BuyerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BuyerMongoRepository extends MongoRepository<BuyerDocument, String> {
    Optional<BuyerDocument> findByUserId(String userId);
}
