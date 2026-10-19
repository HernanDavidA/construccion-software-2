package com.nexusmarket.infrastructure.adapter.outbound.persistence.repository;

import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
