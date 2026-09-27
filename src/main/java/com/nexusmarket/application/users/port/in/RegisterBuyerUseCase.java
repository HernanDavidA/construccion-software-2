package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Address;
import com.nexusmarket.domain.users.Buyer;

public interface RegisterBuyerUseCase {

    Buyer execute(String id, String identityDocument, String fullName, String email, Address primaryAddress);
}
