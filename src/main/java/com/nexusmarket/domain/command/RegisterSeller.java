package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.Address;

public record RegisterSeller(
        String fullName,
        String email,
        String identityDocument,
        String tradeName,
        String warehouseName,
        Address warehouseAddress) {
}
