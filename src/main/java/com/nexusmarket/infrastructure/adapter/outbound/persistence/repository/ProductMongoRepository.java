package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {
    List<ProductDocument> findByStatus(ProductStatus status);

    List<ProductDocument> findBySellerId(String sellerId);
}
