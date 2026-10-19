package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.AdjustStock;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.User;

public interface AdjustStockUseCase {
    Inventory adjust(User actor, AdjustStock command);
}
