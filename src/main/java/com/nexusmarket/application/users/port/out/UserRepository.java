package com.nexusmarket.application.users.port.out;

import com.nexusmarket.domain.users.User;

import java.util.Optional;

/**
 * Persistence port for the user hierarchy and platform-wide uniqueness (validation 11).
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(String id);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
