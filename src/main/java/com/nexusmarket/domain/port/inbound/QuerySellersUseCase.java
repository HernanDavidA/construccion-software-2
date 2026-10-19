package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QuerySellersUseCase {
    Seller get(User actor, String sellerId);

    List<Seller> list(User actor);
}
