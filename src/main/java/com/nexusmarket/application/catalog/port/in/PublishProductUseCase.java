package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Product;

public interface PublishProductUseCase {

    Product execute(String productId);
}
