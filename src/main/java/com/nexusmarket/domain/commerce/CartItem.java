package com.nexusmarket.domain.commerce;

import com.nexusmarket.domain.catalog.Product;
import com.nexusmarket.domain.catalog.ProductVariant;

import java.util.Objects;

/**
 * Provisional selection line in the cart.
 */
public class CartItem {

    private final Product product;
    private final ProductVariant variant;
    private int quantity;

    public CartItem(Product product, ProductVariant variant, int quantity) {
        this.product = Objects.requireNonNull(product, "product is required");
        this.variant = variant;
        setQuantity(quantity);
        if (!product.isPublished()) {
            throw new IllegalStateException("only published products can be added");
        }
    }

    public Product getProduct() {
        return product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        this.quantity = quantity;
    }
}
