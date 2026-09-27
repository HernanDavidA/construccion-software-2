package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Warehouse;

public interface CreateMarketplaceWarehouseUseCase {

    Warehouse execute(String id, String name, String location);
}
