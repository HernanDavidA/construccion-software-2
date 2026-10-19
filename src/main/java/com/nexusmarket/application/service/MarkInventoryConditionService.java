package com.nexusmarket.application.service;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryCondition;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.MarkInventoryConditionUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class MarkInventoryConditionService implements MarkInventoryConditionUseCase {

    private final AccessPolicy accessPolicy;
    private final InventoryRepository inventory;
    private final InventoryAuthorization authorization;

    public MarkInventoryConditionService(AccessPolicy accessPolicy, InventoryRepository inventory, InventoryAuthorization authorization) {
        this.accessPolicy = accessPolicy;
        this.inventory = inventory;
        this.authorization = authorization;
    }

    @Override
    public Inventory mark(User actor, String inventoryId, InventoryCondition condition) {
        accessPolicy.requireRole(actor, Role.SELLER, Role.LOGISTICS_OPERATOR);
        Inventory item = authorization.find(inventoryId);
        authorization.requireOnInventory(actor, item);
        item.markCondition(condition);
        return inventory.save(item);
    }
}
