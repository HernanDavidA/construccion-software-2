package com.nexusmarket.application.commerce.port.in;

import com.nexusmarket.domain.catalog.ProductVariant;
import com.nexusmarket.domain.commerce.Cart;

public interface AddItemToCartUseCase {

    Cart execute(String cartId, String productId, ProductVariant variant, int quantity);
}
