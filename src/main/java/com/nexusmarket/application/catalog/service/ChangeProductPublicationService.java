package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.ChangeProductPublicationUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductStatus;

import java.util.Objects;

/**
 * Suspends or discontinues a product (OBJ-05).
 */
public class ChangeProductPublicationService implements ChangeProductPublicationUseCase {

    private final ProductRepository products;

    public ChangeProductPublicationService(ProductRepository products) {
        this.products = Objects.requireNonNull(products);
    }

    public Product execute(String productId, ProductStatus status) {
        if (status != ProductStatus.SUSPENDED && status != ProductStatus.DISCONTINUED) {
            throw new IllegalArgumentException("status must be SUSPENDED or DISCONTINUED");
        }
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        product.setStatus(status);
        products.save(product);
        return product;
    }
}
