package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryBuyersUseCase {
    Buyer getOwn(User actor);

    Buyer get(User actor, String buyerId);

    List<Buyer> list(User actor);
}
