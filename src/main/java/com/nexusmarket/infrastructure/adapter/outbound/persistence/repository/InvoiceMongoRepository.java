package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InvoiceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InvoiceMongoRepository extends MongoRepository<InvoiceDocument, String> {
    Optional<InvoiceDocument> findByOrderId(String orderId);
}
