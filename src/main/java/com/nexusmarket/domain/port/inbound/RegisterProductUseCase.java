package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RegisterProduct;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.User;

public interface RegisterProductUseCase {
    Product register(User actor, RegisterProduct command);
}
