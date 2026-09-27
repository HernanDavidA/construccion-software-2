package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.PublishProductUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductStatus;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;

import java.util.Objects;

/**
 * Publishes a product if the owning seller is active and the product is not discontinued (OBJ-05).
 */
public class PublishProductService implements PublishProductUseCase {

    private final ProductRepository products;

    public PublishProductService(ProductRepository products) {
        this.products = Objects.requireNonNull(products);
    }

    public Product execute(String productId) {
        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("product not found"));
        if (product.getStatus() == ProductStatus.DISCONTINUED) {
            throw new IllegalStateException("a discontinued product cannot be published");
        }
        Seller seller = product.getSeller();
        if (!seller.isActive() || seller.getSellerStatus() != SellerStatus.ACTIVE) {
            throw new IllegalStateException("the seller is not active");
        }
        product.setStatus(ProductStatus.PUBLISHED);
        products.save(product);
        return product;
    }
}
