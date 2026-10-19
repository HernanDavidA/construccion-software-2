package com.nexusmarket.domain.model;

import java.math.BigDecimal;

public record OrderLine(
        String productId,
        String variantId,
        String warehouseId,
        int quantity,
        BigDecimal unitPrice,
        ProductType productType) {

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
