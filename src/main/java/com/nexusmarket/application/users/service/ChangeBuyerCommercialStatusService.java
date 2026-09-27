package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.ChangeBuyerCommercialStatusUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.CommercialStatus;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Enables or suspends a buyer commercially, which determines {@code canBuy()}.
 */
public class ChangeBuyerCommercialStatusService implements ChangeBuyerCommercialStatusUseCase {

    private final UserRepository users;

    public ChangeBuyerCommercialStatusService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Buyer execute(String buyerId, CommercialStatus commercialStatus) {
        User user = users.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("buyer not found"));
        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException("user is not a buyer");
        }
        buyer.setCommercialStatus(commercialStatus);
        users.save(buyer);
        return buyer;
    }
}
