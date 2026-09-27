package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.RegisterBuyerUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.CommercialStatus;
import com.nexusmarket.domain.users.UserStatus;

import java.util.Objects;

/**
 * Registers a buyer with unique identity and purchase eligibility (OBJ-03, validation 11).
 */
public class RegisterBuyerService implements RegisterBuyerUseCase {

    private final UserRepository users;

    public RegisterBuyerService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Buyer execute(String id, String identityDocument, String fullName,
                         String email, Address primaryAddress) {
        requireUniqueIdentity(identityDocument, email);
        Buyer buyer = new Buyer(id, identityDocument, fullName, email,
                UserStatus.ACTIVE, primaryAddress, CommercialStatus.ENABLED);
        users.save(buyer);
        return buyer;
    }

    private void requireUniqueIdentity(String identityDocument, String email) {
        if (users.existsByEmail(email) || users.existsByIdentityDocument(identityDocument)) {
            throw new IllegalStateException("email or identity document already registered");
        }
    }
}
