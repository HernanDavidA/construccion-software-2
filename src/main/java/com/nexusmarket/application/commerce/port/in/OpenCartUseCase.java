package com.nexusmarket.application.commerce.port.in;

import com.nexusmarket.domain.commerce.Cart;

public interface OpenCartUseCase {

    Cart execute(String buyerId);
}
