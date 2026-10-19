package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;

import java.util.List;

public interface QueryWarehousesUseCase {
    Warehouse get(User actor, String warehouseId);

    List<Warehouse> list(User actor);
}
