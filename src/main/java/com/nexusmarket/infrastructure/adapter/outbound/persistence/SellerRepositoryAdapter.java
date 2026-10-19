package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.SellerDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.SellerMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SellerRepositoryAdapter implements SellerRepository {

    private final SellerMongoRepository mongo;

    public SellerRepositoryAdapter(SellerMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Seller save(Seller seller) {
        return mongo.save(SellerDocument.from(seller)).toDomain();
    }

    @Override
    public Optional<Seller> findById(String id) {
        return mongo.findById(id).map(SellerDocument::toDomain);
    }

    @Override
    public Optional<Seller> findByUserId(String userId) {
        return mongo.findByUserId(userId).map(SellerDocument::toDomain);
    }

    @Override
    public List<Seller> findAll() {
        return mongo.findAll().stream().map(SellerDocument::toDomain).toList();
    }
}
