package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.BuyerDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.BuyerMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BuyerRepositoryAdapter implements BuyerRepository {

    private final BuyerMongoRepository mongo;

    public BuyerRepositoryAdapter(BuyerMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Buyer save(Buyer buyer) {
        return mongo.save(BuyerDocument.from(buyer)).toDomain();
    }

    @Override
    public Optional<Buyer> findById(String id) {
        return mongo.findById(id).map(BuyerDocument::toDomain);
    }

    @Override
    public Optional<Buyer> findByUserId(String userId) {
        return mongo.findByUserId(userId).map(BuyerDocument::toDomain);
    }

    @Override
    public List<Buyer> findAll() {
        return mongo.findAll().stream().map(BuyerDocument::toDomain).toList();
    }
}
