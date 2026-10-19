package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.ReceiveStock;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.User;

public interface ReceiveStockUseCase {
    Inventory receive(User actor, ReceiveStock command);
}
