package com.nexusmarket.domain.command;

public record ReceiveStock(String productId, String variantId, String warehouseId, int quantity) {
}
