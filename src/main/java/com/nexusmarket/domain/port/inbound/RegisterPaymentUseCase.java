package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.User;

public interface RegisterPaymentUseCase {
    Order register(User actor, String orderId);
}
