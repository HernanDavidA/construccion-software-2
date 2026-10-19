package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderLine;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.RegisterPaymentUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class RegisterPaymentService implements RegisterPaymentUseCase {

    private final AccessPolicy accessPolicy;
    private final OrderRepository orders;
    private final BuyerRepository buyers;
    private final InventoryRepository inventory;
    private final InvoiceRepository invoices;
    private final ShipmentRepository shipments;

    public RegisterPaymentService(
            AccessPolicy accessPolicy,
            OrderRepository orders,
            BuyerRepository buyers,
            InventoryRepository inventory,
            InvoiceRepository invoices,
            ShipmentRepository shipments) {
        this.accessPolicy = accessPolicy;
        this.orders = orders;
        this.buyers = buyers;
        this.inventory = inventory;
        this.invoices = invoices;
        this.shipments = shipments;
    }

    @Override
    public Order register(User actor, String orderId) {
        accessPolicy.requireRole(actor, Role.BUYER, Role.ADMINISTRATOR);
        Order order = orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (actor.getRole() == Role.BUYER) {
            requireOwner(actor, order);
        }
        if (invoices.findByOrderId(order.getId()).isPresent()) {
            throw new BusinessRuleException("El pedido ya tiene factura");
        }
        order.advanceTo(OrderStatus.PAID);
        for (OrderLine line : order.getLines()) {
            if (line.productType() != ProductType.PHYSICAL) {
                continue;
            }
            var stock = inventory.findByProductVariantAndWarehouse(line.productId(), line.variantId(), line.warehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario reservado no encontrado"));
            var movement = stock.saleOutbound(line.quantity(), order.getId());
            inventory.save(stock);
            inventory.saveMovement(movement);
        }
        invoices.save(Invoice.issue(order));
        if (order.containsPhysicalProduct()) {
            Buyer buyer = buyers.findById(order.getBuyerId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
            shipments.save(Shipment.prepare(order.getId(), buyer.getPrimaryAddress()));
        } else {
            order.advanceTo(OrderStatus.COMPLETED);
        }
        return orders.save(order);
    }

    private void requireOwner(User actor, Order order) {
        Buyer buyer = buyers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
        if (!buyer.getId().equals(order.getBuyerId())) {
            throw new AccessDeniedException("El comprador no puede administrar pedidos de otros compradores");
        }
    }
}
