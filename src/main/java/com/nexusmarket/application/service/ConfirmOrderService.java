package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.model.CartItem;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.OrderLine;
import com.nexusmarket.domain.model.OrderStatus;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ConfirmOrderUseCase;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.CartRepository;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final AccessPolicy accessPolicy;
    private final BuyerRepository buyers;
    private final CartRepository carts;
    private final ProductRepository products;
    private final InventoryRepository inventory;
    private final OrderRepository orders;

    public ConfirmOrderService(
            AccessPolicy accessPolicy,
            BuyerRepository buyers,
            CartRepository carts,
            ProductRepository products,
            InventoryRepository inventory,
            OrderRepository orders) {
        this.accessPolicy = accessPolicy;
        this.buyers = buyers;
        this.carts = carts;
        this.products = products;
        this.inventory = inventory;
        this.orders = orders;
    }

    @Override
    public Order confirm(User actor) {
        accessPolicy.requireRole(actor, Role.BUYER);
        Buyer buyer = buyers.findByUserId(actor.getId()).orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado"));
        if (buyer.getCommercialStatus() != BuyerCommercialStatus.ENABLED) {
            throw new BusinessRuleException("El comprador no está habilitado para comprar");
        }
        Cart cart = carts.findByBuyerId(buyer.getId()).orElseThrow(() -> new BusinessRuleException("El carrito no tiene productos"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException("El carrito no tiene productos");
        }

        String orderId = UUID.randomUUID().toString();
        List<OrderLine> lines = new ArrayList<>();
        List<Inventory> reserved = new ArrayList<>();
        List<InventoryMovement> movements = new ArrayList<>();
        boolean needsAddress = false;

        for (CartItem item : cart.getItems()) {
            Product product = products.findById(item.productId()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            if (product.getStatus() != ProductStatus.PUBLISHED) {
                throw new BusinessRuleException("Solo se pueden comprar productos publicados");
            }
            product.requireVariant(item.variantId());
            String warehouseId = null;
            if (product.getType() == ProductType.PHYSICAL) {
                needsAddress = true;
                Inventory stock = inventory.findWithAvailability(product.getId(), item.variantId(), item.quantity())
                        .orElseThrow(() -> new BusinessRuleException("No hay existencias suficientes para " + product.getName()));
                movements.add(stock.reserve(item.quantity(), orderId));
                reserved.add(stock);
                warehouseId = stock.getWarehouseId();
            }
            lines.add(new OrderLine(product.getId(), item.variantId(), warehouseId, item.quantity(), product.getPrice(), product.getType()));
        }

        if (needsAddress && buyer.getPrimaryAddress() == null) {
            throw new BusinessRuleException("La dirección principal es obligatoria para productos físicos");
        }

        Order order = orders.save(new Order(orderId, buyer.getId(), OrderStatus.PENDING_PAYMENT, lines, Instant.now()));
        reserved.forEach(inventory::save);
        movements.forEach(inventory::saveMovement);
        cart.clear();
        carts.save(cart);
        return order;
    }
}
