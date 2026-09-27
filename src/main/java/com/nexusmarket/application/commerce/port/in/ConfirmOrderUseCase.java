package com.nexusmarket.application.commerce.port.in;

import com.nexusmarket.domain.commerce.Order;

public interface ConfirmOrderUseCase {

    Order execute(String cartId, String orderId);
}
