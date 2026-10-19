package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.Address;

import java.util.List;

public record RegisterBuyer(
        String fullName,
        String email,
        String identityDocument,
        Address primaryAddress,
        List<Address> additionalAddresses) {
}
