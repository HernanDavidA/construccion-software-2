package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.AdjustStock;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.AdjustStockUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class AdjustStockService implements AdjustStockUseCase {

    private final AccessPolicy accessPolicy;
    private final InventoryRepository inventory;
    private final InventoryAuthorization authorization;

    public AdjustStockService(AccessPolicy accessPolicy, InventoryRepository inventory, InventoryAuthorization authorization) {
        this.accessPolicy = accessPolicy;
        this.inventory = inventory;
        this.authorization = authorization;
    }

    @Override
    public Inventory adjust(User actor, AdjustStock command) {
        accessPolicy.requireRole(actor, Role.SELLER, Role.LOGISTICS_OPERATOR);
        Inventory item = authorization.find(command.inventoryId());
        authorization.requireOnInventory(actor, item);
        var movement = item.adjust(command.delta(), "AJUSTE");
        if (command.condition() != null) {
            item.markCondition(command.condition());
        }
        inventory.save(item);
        inventory.saveMovement(movement);
        return item;
    }
}
