package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.OnboardSellerUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Administrator;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Onboards a seller through an active administrator. Sellers cannot self-register (OBJ-02).
 */
public class OnboardSellerService implements OnboardSellerUseCase {

    private final UserRepository users;

    public OnboardSellerService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Seller execute(String administratorId, String sellerId, String identityDocument,
                          String fullName, String email, String businessName) {
        if (users.existsByEmail(email) || users.existsByIdentityDocument(identityDocument)) {
            throw new IllegalStateException("email or identity document already registered");
        }
        User actor = users.findById(administratorId)
                .orElseThrow(() -> new IllegalArgumentException("administrator not found"));
        if (!(actor instanceof Administrator administrator)) {
            throw new IllegalStateException("only an administrator can onboard a seller");
        }
        Seller seller = administrator.onboardSeller(sellerId, identityDocument, fullName, email, businessName);
        users.save(seller);
        return seller;
    }
}
