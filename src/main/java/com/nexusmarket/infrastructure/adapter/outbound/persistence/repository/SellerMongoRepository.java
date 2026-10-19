package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.SellerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SellerMongoRepository extends MongoRepository<SellerDocument, String> {
    Optional<SellerDocument> findByUserId(String userId);
}
