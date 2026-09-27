package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.AddProductVariantUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductVariant;

import java.util.Objects;

/**
 * Adds a differentiating variant to an existing product (OBJ-05).
 */
public class AddProductVariantService implements AddProductVariantUseCase {

    private final ProductRepository products;

    public AddProductVariantService(ProductRepository products) {
        this.products = Objects.requireNonNull(products);
    }

    public Product execute(String productId, String variantId, String color, String size, String model) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        product.addVariant(new ProductVariant(variantId, color, size, model));
        products.save(product);
        return product;
    }
}
