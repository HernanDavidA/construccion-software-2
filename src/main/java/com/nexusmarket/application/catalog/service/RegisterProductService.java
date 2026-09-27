package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.RegisterProductUseCase;
import com.nexusmarket.application.catalog.port.out.ProductRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductStatus;
import com.nexusmarket.domain.catalog.ProductType;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;
import com.nexusmarket.domain.users.User;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Registers a product for an active seller. Starts suspended until published (OBJ-05).
 */
public class RegisterProductService implements RegisterProductUseCase {

    private final ProductRepository products;
    private final UserRepository users;

    public RegisterProductService(ProductRepository products, UserRepository users) {
        this.products = Objects.requireNonNull(products);
        this.users = Objects.requireNonNull(users);
    }

    public Product execute(String id, String name, ProductType type, String sellerId, BigDecimal price) {
        User user = users.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("seller not found"));
        if (!(user instanceof Seller seller)) {
            throw new IllegalStateException("user is not a seller");
        }
        if (!seller.isActive() || seller.getSellerStatus() != SellerStatus.ACTIVE) {
            throw new IllegalStateException("only an active seller can register a product");
        }
        Product product = new Product(id, name, type, ProductStatus.SUSPENDED, seller, price);
        products.save(product);
        return product;
    }
}
