package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.User;

public interface ChangeProductStatusUseCase {
    Product change(User actor, String productId, ProductStatus status);
}
