package com.nexusmarket.domain.commerce;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductType;
import com.nexusmarket.domain.catalog.ProductVariant;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Confirmed line of an order. Preserves price and description at purchase time.
 */
public class OrderItem {

    private final String productId;
    private final String productName;
    private final ProductType productType;
    private final ProductVariant variant;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Product product, ProductVariant variant, int quantity) {
        Objects.requireNonNull(product, "product is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.productId = product.getId();
        this.productName = product.getName();
        this.productType = product.getType();
        this.variant = variant;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public boolean isPhysical() {
        return productType == ProductType.PHYSICAL;
    }
}
