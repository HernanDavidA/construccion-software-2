package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Warehouse;

public interface CreateSellerWarehouseUseCase {

    Warehouse execute(String id, String name, String sellerId, String location);
}
