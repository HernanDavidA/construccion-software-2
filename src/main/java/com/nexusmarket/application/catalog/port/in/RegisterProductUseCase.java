package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductType;

import java.math.BigDecimal;

public interface RegisterProductUseCase {

    Product execute(String id, String name, ProductType type, String sellerId, BigDecimal price);
}
