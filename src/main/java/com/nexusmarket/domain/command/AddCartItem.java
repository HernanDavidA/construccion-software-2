package com.nexusmarket.domain.command;

public record AddCartItem(String productId, String variantId, int quantity) {
}
