package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;

public interface AddBuyerAddressUseCase {

    Buyer execute(String buyerId, Address address);
}
