package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.UserDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SellerRepositoryAdapter implements SellerRepository {

    private final UserMongoRepository mongo;

    public SellerRepositoryAdapter(UserMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Seller save(Seller seller) {
        return (Seller) mongo.save(UserDocument.from(seller)).toDomain();
    }

    @Override
    public Optional<Seller> findById(String id) {
        return mongo.findById(id).map(UserDocument::toDomain).filter(Seller.class::isInstance).map(Seller.class::cast);
    }

    @Override
    public List<Seller> findAll() {
        return mongo.findByRole(Role.SELLER).stream().map(document -> (Seller) document.toDomain()).toList();
    }
}
