package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryOrdersUseCase {
    Order get(User actor, String orderId);

    List<Order> list(User actor);
}
