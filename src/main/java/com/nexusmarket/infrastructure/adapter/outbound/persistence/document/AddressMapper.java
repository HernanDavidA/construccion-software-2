package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Address;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static AddressDocument from(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDocument(address.line(), address.city(), address.region(), address.country());
    }

    public static Address toDomain(AddressDocument document) {
        if (document == null) {
            return null;
        }
        return new Address(document.getLine(), document.getCity(), document.getRegion(), document.getCountry());
    }
}
