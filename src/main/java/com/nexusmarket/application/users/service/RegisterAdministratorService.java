package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.RegisterAdministratorUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Administrator;
import com.nexusmarket.domain.users.UserStatus;

import java.util.Objects;

/**
 * Registers an administrator with unique identity (OBJ-01, RG-02).
 */
public class RegisterAdministratorService implements RegisterAdministratorUseCase {

    private final UserRepository users;

    public RegisterAdministratorService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Administrator execute(String id, String identityDocument, String fullName, String email) {
        if (users.existsByEmail(email) || users.existsByIdentityDocument(identityDocument)) {
            throw new IllegalStateException("email or identity document already registered");
        }
        Administrator administrator = new Administrator(id, identityDocument, fullName, email, UserStatus.ACTIVE);
        users.save(administrator);
        return administrator;
    }
}
