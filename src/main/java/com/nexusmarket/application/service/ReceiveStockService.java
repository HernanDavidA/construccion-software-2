package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.ReceiveStock;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.port.inbound.ReceiveStockUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ReceiveStockService implements ReceiveStockUseCase {

    private final AccessPolicy accessPolicy;
    private final InventoryRepository inventory;
    private final ProductRepository products;
    private final WarehouseRepository warehouses;
    private final InventoryAuthorization authorization;

    public ReceiveStockService(
            AccessPolicy accessPolicy,
            InventoryRepository inventory,
            ProductRepository products,
            WarehouseRepository warehouses,
            InventoryAuthorization authorization) {
        this.accessPolicy = accessPolicy;
        this.inventory = inventory;
        this.products = products;
        this.warehouses = warehouses;
        this.authorization = authorization;
    }

    @Override
    public Inventory receive(User actor, ReceiveStock command) {
        accessPolicy.requireRole(actor, Role.SELLER, Role.LOGISTICS_OPERATOR);
        Product product = products.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (product.getType() != ProductType.PHYSICAL) {
            throw new BusinessRuleException("Los productos digitales no requieren inventario");
        }
        product.requireVariant(command.variantId());
        Warehouse warehouse = warehouses.findById(command.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada"));
        authorization.requireOperation(actor, product, warehouse);
        Inventory item = inventory
                .findByProductVariantAndWarehouse(product.getId(), command.variantId(), warehouse.getId())
                .orElseGet(() -> Inventory.create(product.getId(), command.variantId(), warehouse.getId()));
        var movement = item.receive(command.quantity(), "INGRESO");
        inventory.save(item);
        inventory.saveMovement(movement);
        return item;
    }
}
