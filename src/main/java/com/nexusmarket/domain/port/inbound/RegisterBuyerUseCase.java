package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RegisterBuyer;
import com.nexusmarket.domain.model.Buyer;

public interface RegisterBuyerUseCase {
    Buyer register(RegisterBuyer command);
}
