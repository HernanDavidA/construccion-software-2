package com.nexusmarket.application.users.service;

import com.nexusmarket.application.users.port.in.AddBuyerAddressUseCase;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Adds an additional delivery address to the owning buyer.
 */
public class AddBuyerAddressService implements AddBuyerAddressUseCase {

    private final UserRepository users;

    public AddBuyerAddressService(UserRepository users) {
        this.users = Objects.requireNonNull(users);
    }

    public Buyer execute(String buyerId, Address address) {
        User user = users.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("buyer not found"));
        if (!(user instanceof Buyer buyer)) {
            throw new IllegalStateException("user is not a buyer");
        }
        buyer.addAdditionalAddress(address);
        users.save(buyer);
        return buyer;
    }
}
