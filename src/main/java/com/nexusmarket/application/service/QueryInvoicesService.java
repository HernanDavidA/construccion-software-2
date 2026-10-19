package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryInvoicesUseCase;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryInvoicesService implements QueryInvoicesUseCase {

    private final AccessPolicy accessPolicy;
    private final InvoiceRepository invoices;
    private final OrderRepository orders;
    private final ProductRepository products;

    public QueryInvoicesService(
            AccessPolicy accessPolicy,
            InvoiceRepository invoices,
            OrderRepository orders,
            ProductRepository products) {
        this.accessPolicy = accessPolicy;
        this.invoices = invoices;
        this.orders = orders;
        this.products = products;
    }

    @Override
    public Invoice getByOrder(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.SELLER, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Invoice invoice = invoices.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        if (!canView(actor, invoice)) {
            throw new AccessDeniedException("RG-03: el participante no puede consultar esta factura");
        }
        return invoice;
    }

    @Override
    public List<Invoice> list(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.SELLER, Role.ADMINISTRATOR, Role.SUPERVISOR);
        return invoices.findAll().stream().filter(invoice -> canView(actor, invoice)).toList();
    }

    private boolean canView(User actor, Invoice invoice) {
        if (actor.getRole() == Role.ADMINISTRATOR || actor.getRole() == Role.SUPERVISOR) {
            return true;
        }
        if (actor instanceof Buyer) {
            return actor.getId().equals(invoice.getBuyerId());
        }
        if (!(actor instanceof Seller seller)) {
            return false;
        }
        Order order = orders.findById(invoice.getOrderId()).orElse(null);
        if (order == null) {
            return false;
        }
        return order.getLines().stream().anyMatch(line -> products.findById(line.productId())
                .map(product -> product.belongsTo(seller.getId()))
                .orElse(false));
    }
}
