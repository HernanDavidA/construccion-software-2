package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.UpdateAddresses;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.User;

public interface UpdateBuyerAddressesUseCase {
    Buyer update(User actor, UpdateAddresses command);
}
