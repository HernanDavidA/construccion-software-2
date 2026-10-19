package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {

    private final String id;
    private final String sellerId;
    private final String name;
    private final String description;
    private final ProductType type;
    private final BigDecimal price;
    private final List<ProductVariant> variants;
    private ProductStatus status;

    public Product(
            String id,
            String sellerId,
            String name,
            String description,
            ProductType type,
            BigDecimal price,
            List<ProductVariant> variants,
            ProductStatus status) {
        this.id = RequiredText.require(id, "El identificador del producto es obligatorio");
        this.sellerId = RequiredText.require(sellerId, "El producto debe pertenecer a un vendedor");
        this.name = RequiredText.require(name, "El nombre del producto es obligatorio");
        this.description = description == null ? "" : description.trim();
        if (type == null) {
            throw new BusinessRuleException("El tipo de producto es obligatorio");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El precio del producto debe ser mayor que cero");
        }
        if (status == null) {
            throw new BusinessRuleException("El estado del producto es obligatorio");
        }
        this.type = type;
        this.price = price;
        this.status = status;
        this.variants = new ArrayList<>();
        if (variants != null) {
            this.variants.addAll(variants);
        }
    }

    public void changeStatus(ProductStatus newStatus) {
        if (newStatus == null) {
            throw new BusinessRuleException("El estado del producto es obligatorio");
        }
        this.status = newStatus;
    }

    public void requireVariant(String variantId) {
        String normalized = VariantIds.normalize(variantId);
        if (variants.isEmpty()) {
            if (normalized != null) {
                throw new BusinessRuleException("El producto no maneja variantes");
            }
            return;
        }
        boolean exists = variants.stream().anyMatch(variant -> variant.id().equals(normalized));
        if (!exists) {
            throw new BusinessRuleException("La variante no pertenece al producto");
        }
    }

    public boolean belongsTo(String sellerId) {
        return this.sellerId.equals(sellerId);
    }

    public String getId() {
        return id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductType getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    public ProductStatus getStatus() {
        return status;
    }
}
