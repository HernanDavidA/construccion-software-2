package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ProductDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.ProductMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository mongo;

    public ProductRepositoryAdapter(ProductMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Product save(Product product) {
        return mongo.save(ProductDocument.from(product)).toDomain();
    }

    @Override
    public Optional<Product> findById(String id) {
        return mongo.findById(id).map(ProductDocument::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return mongo.findAll().stream().map(ProductDocument::toDomain).toList();
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return mongo.findByStatus(status).stream().map(ProductDocument::toDomain).toList();
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        return mongo.findBySellerId(sellerId).stream().map(ProductDocument::toDomain).toList();
    }
}
