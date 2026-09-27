package com.nexusmarket.application.users.port.in;

import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;

public interface ChangeSellerStatusUseCase {

    Seller execute(String sellerId, SellerStatus sellerStatus);
}
