package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.RegisterSupervisorUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Supervisor;
import com.nexusmarket.domain.users.UserStatus;

import java.util.Objects;

/**
 * Registers a supervisor. This role is not an actor on write use cases (RG-03).
 */
public class RegisterSupervisorService implements RegisterSupervisorUseCase {

    private final UserRepository users;

    public RegisterSupervisorService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Supervisor execute(String id, String identityDocument, String fullName, String email) {
        if (users.existsByEmail(email) || users.existsByIdentityDocument(identityDocument)) {
            throw new IllegalStateException("email or identity document already registered");
        }
        Supervisor supervisor = new Supervisor(id, identityDocument, fullName, email, UserStatus.ACTIVE);
        users.save(supervisor);
        return supervisor;
    }
}
