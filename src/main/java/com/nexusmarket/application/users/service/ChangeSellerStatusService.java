package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.ChangeSellerStatusUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Activates or suspends a seller. A suspended seller cannot publish or receive stock.
 */
public class ChangeSellerStatusService implements ChangeSellerStatusUseCase {

    private final UserRepository users;

    public ChangeSellerStatusService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Seller execute(String sellerId, SellerStatus sellerStatus) {
        User user = users.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("seller not found"));
        if (!(user instanceof Seller seller)) {
            throw new IllegalStateException("user is not a seller");
        }
        seller.setSellerStatus(sellerStatus);
        users.save(seller);
        return seller;
    }
}
