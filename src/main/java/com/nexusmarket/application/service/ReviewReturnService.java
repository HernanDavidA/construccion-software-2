package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderLine;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.model.RefundStatus;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ReviewReturnUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.RefundRepository;
import com.nexusmarket.domain.port.outbound.ReturnRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewReturnService implements ReviewReturnUseCase {

    private final AccessPolicy accessPolicy;
    private final ReturnRepository returns;
    private final OrderRepository orders;
    private final InvoiceRepository invoices;
    private final InventoryRepository inventory;
    private final RefundRepository refunds;

    public ReviewReturnService(
            AccessPolicy accessPolicy,
            ReturnRepository returns,
            OrderRepository orders,
            InvoiceRepository invoices,
            InventoryRepository inventory,
            RefundRepository refunds) {
        this.accessPolicy = accessPolicy;
        this.returns = returns;
        this.orders = orders;
        this.invoices = invoices;
        this.inventory = inventory;
        this.refunds = refunds;
    }

    @Override
    public ReturnRequest approve(User actor, String returnId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        ReturnRequest returnRequest = find(returnId);
        returnRequest.approve();
        Order order = orders.findById(returnRequest.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        restock(order);
        var invoice = invoices.findByOrderId(order.getId()).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        refunds.save(new Refund(UUID.randomUUID().toString(), returnRequest.getId(), invoice.getId(), invoice.getTotal(), RefundStatus.PENDING));
        return returns.save(returnRequest);
    }

    @Override
    public ReturnRequest reject(User actor, String returnId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        ReturnRequest returnRequest = find(returnId);
        returnRequest.reject();
        return returns.save(returnRequest);
    }

    private void restock(Order order) {
        for (OrderLine line : order.getLines()) {
            if (line.productType() != ProductType.PHYSICAL) {
                continue;
            }
            var stock = inventory.findByProductVariantAndWarehouse(line.productId(), line.variantId(), line.warehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado"));
            var movement = stock.returnStock(line.quantity(), order.getId());
            inventory.save(stock);
            inventory.saveMovement(movement);
        }
    }

    private ReturnRequest find(String returnId) {
        return returns.findById(returnId).orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada"));
    }
}
