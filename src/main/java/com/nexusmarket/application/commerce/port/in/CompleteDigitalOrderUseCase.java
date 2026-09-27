package com.nexusmarket.application.commerce.port.in;

import com.nexusmarket.domain.commerce.Order;

public interface CompleteDigitalOrderUseCase {

    Order execute(String orderId);
}
