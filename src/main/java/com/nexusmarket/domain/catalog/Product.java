package com.nexusmarket.domain.catalog;

import com.nexusmarket.domain.users.Seller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Physical or digital good offered by a seller (OBJ-05).
 */
public class Product {

    private final String id;
    private String name;
    private final ProductType type;
    private ProductStatus status;
    private final Seller seller;
    private BigDecimal price;
    private final List<ProductVariant> variants;

    public Product(String id, String name, ProductType type, ProductStatus status,
                   Seller seller, BigDecimal price) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.type = Objects.requireNonNull(type, "type is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.seller = Objects.requireNonNull(seller, "seller is required");
        this.price = requirePrice(price);
        this.variants = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "name");
    }

    public ProductType getType() {
        return type;
    }

    public boolean isPhysical() {
        return type == ProductType.PHYSICAL;
    }

    public boolean isDigital() {
        return type == ProductType.DIGITAL;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = Objects.requireNonNull(status, "status is required");
    }

    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }

    public Seller getSeller() {
        return seller;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = requirePrice(price);
    }

    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    public void addVariant(ProductVariant variant) {
        variants.add(Objects.requireNonNull(variant, "variant is required"));
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }

    private static BigDecimal requirePrice(BigDecimal price) {
        Objects.requireNonNull(price, "price is required");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }
        return price;
    }
}
