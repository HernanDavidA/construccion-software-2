package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RegisterWarehouse;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;

public interface RegisterWarehouseUseCase {
    Warehouse register(User actor, RegisterWarehouse command);
}
