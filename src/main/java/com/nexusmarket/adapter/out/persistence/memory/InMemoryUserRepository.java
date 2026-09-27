package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new LinkedHashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return users.values().stream().anyMatch(user -> user.getEmail().equals(normalized));
    }

    @Override
    public boolean existsByIdentityDocument(String identityDocument) {
        return users.values().stream()
                .anyMatch(user -> user.getIdentityDocument().equals(identityDocument));
    }
}
