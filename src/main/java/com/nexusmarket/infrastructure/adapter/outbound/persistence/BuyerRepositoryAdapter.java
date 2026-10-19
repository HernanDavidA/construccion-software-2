package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.UserDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BuyerRepositoryAdapter implements BuyerRepository {

    private final UserMongoRepository mongo;

    public BuyerRepositoryAdapter(UserMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Buyer save(Buyer buyer) {
        return (Buyer) mongo.save(UserDocument.from(buyer)).toDomain();
    }

    @Override
    public Optional<Buyer> findById(String id) {
        return mongo.findById(id).map(UserDocument::toDomain).filter(Buyer.class::isInstance).map(Buyer.class::cast);
    }

    @Override
    public List<Buyer> findAll() {
        return mongo.findByRole(Role.BUYER).stream().map(document -> (Buyer) document.toDomain()).toList();
    }
}
