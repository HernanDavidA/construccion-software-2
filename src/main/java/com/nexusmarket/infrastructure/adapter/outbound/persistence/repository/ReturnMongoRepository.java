package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ReturnRequestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReturnMongoRepository extends MongoRepository<ReturnRequestDocument, String> {
    List<ReturnRequestDocument> findByOrderId(String orderId);
}
