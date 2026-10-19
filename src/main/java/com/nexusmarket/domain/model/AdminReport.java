package com.nexusmarket.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record AdminReport(
        List<OrdersByStatus> ordersByStatus,
        List<WarehouseStock> inventory,
        List<BilledSale> sales) {

    public record OrdersByStatus(OrderStatus status, long count) {
    }

    public record WarehouseStock(
            String productId,
            String warehouseId,
            int available,
            int reserved,
            InventoryCondition condition) {
    }

    public record BilledSale(String invoiceId, String orderId, BigDecimal total, InvoiceStatus status) {
    }
}
