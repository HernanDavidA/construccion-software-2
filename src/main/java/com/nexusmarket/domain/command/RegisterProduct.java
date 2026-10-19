package com.nexusmarket.domain.command;

import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

public record RegisterProduct(
        String name,
        String description,
        ProductType type,
        BigDecimal price,
        List<ProductVariant> variants) {
}
