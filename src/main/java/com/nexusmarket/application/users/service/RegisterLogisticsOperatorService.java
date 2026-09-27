package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.RegisterLogisticsOperatorUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.LogisticsOperator;
import com.nexusmarket.domain.users.UserStatus;

import java.util.Objects;

/**
 * Registers a logistics operator with unique identity (OBJ-10).
 */
public class RegisterLogisticsOperatorService implements RegisterLogisticsOperatorUseCase {

    private final UserRepository users;

    public RegisterLogisticsOperatorService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public LogisticsOperator execute(String id, String identityDocument, String fullName, String email) {
        if (users.existsByEmail(email) || users.existsByIdentityDocument(identityDocument)) {
            throw new IllegalStateException("email or identity document already registered");
        }
        LogisticsOperator operator = new LogisticsOperator(id, identityDocument, fullName, email, UserStatus.ACTIVE);
        users.save(operator);
        return operator;
    }
}
