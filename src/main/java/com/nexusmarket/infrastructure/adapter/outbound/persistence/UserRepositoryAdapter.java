package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.outbound.UserRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.UserDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository mongo;

    public UserRepositoryAdapter(UserMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public User save(User user) {
        return mongo.save(UserDocument.from(user)).toDomain();
    }

    @Override
    public Optional<User> findById(String id) {
        return mongo.findById(id).map(UserDocument::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongo.findByEmail(email).map(UserDocument::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongo.existsByEmail(email);
    }

    @Override
    public boolean existsByIdentityDocument(String identityDocument) {
        return mongo.existsByIdentityDocument(identityDocument);
    }

    @Override
    public List<User> findAll() {
        return mongo.findAll().stream().map(UserDocument::toDomain).toList();
    }
}
