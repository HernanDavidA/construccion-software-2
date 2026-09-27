package com.nexusmarket.application.catalog.port.in;

import com.nexusmarket.domain.catalog.Product;

public interface AddProductVariantUseCase {

    Product execute(String productId, String variantId, String color, String size, String model);
}
