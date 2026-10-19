package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.User;

public interface ConfirmOrderUseCase {
    Order confirm(User actor);
}
