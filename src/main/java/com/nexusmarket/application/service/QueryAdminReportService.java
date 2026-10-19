package com.nexusmarket.application.service;

import com.nexusmarket.domain.model.AdminReport;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryAdminReportUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class QueryAdminReportService implements QueryAdminReportUseCase {

    private final AccessPolicy accessPolicy;
    private final OrderRepository orders;
    private final InventoryRepository inventory;
    private final InvoiceRepository invoices;

    public QueryAdminReportService(
            AccessPolicy accessPolicy,
            OrderRepository orders,
            InventoryRepository inventory,
            InvoiceRepository invoices) {
        this.accessPolicy = accessPolicy;
        this.orders = orders;
        this.inventory = inventory;
        this.invoices = invoices;
    }

    @Override
    public AdminReport query(User actor) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR, Role.SUPERVISOR);
        List<AdminReport.OrdersByStatus> byStatus = Arrays.stream(OrderStatus.values())
                .map(status -> new AdminReport.OrdersByStatus(status, orders.findAll().stream().filter(order -> order.getStatus() == status).count()))
                .toList();
        List<AdminReport.WarehouseStock> stock = inventory.findAll().stream()
                .map(item -> new AdminReport.WarehouseStock(
                        item.getProductId(),
                        item.getWarehouseId(),
                        item.getAvailableQuantity(),
                        item.getReservedQuantity(),
                        item.getCondition()))
                .toList();
        List<AdminReport.BilledSale> sales = invoices.findAll().stream()
                .map(invoice -> new AdminReport.BilledSale(invoice.getId(), invoice.getOrderId(), invoice.getTotal(), invoice.getStatus()))
                .toList();
        return new AdminReport(byStatus, stock, sales);
    }
}
