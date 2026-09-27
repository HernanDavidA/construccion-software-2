package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductStatus;

public interface ChangeProductPublicationUseCase {

    Product execute(String productId, ProductStatus status);
}
