package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.Address;

import java.util.List;

public record UpdateAddresses(Address primaryAddress, List<Address> additionalAddresses) {
}
