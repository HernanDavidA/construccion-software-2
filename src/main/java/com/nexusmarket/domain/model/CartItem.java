package com.nexusmarket.domain.model;

public record CartItem(String productId, String variantId, int quantity) {

    public boolean sameProduct(CartItem other) {
        return productId.equals(other.productId) && VariantIds.same(variantId, other.variantId);
    }

    public CartItem plus(int additional) {
        return new CartItem(productId, VariantIds.normalize(variantId), quantity + additional);
    }

    public String normalizedVariantId() {
        return VariantIds.normalize(variantId);
    }
}
