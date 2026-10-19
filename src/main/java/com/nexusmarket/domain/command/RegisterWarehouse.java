package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.WarehouseType;

public record RegisterWarehouse(String name, WarehouseType type, String sellerId, Address address) {
}
