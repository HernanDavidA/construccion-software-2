package com.nexusmarket.application.service;

import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.QueryInventoryUseCase;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryInventoryService implements QueryInventoryUseCase {

    private final AccessPolicy accessPolicy;
    private final InventoryRepository inventory;
    private final InventoryAuthorization authorization;

    public QueryInventoryService(AccessPolicy accessPolicy, InventoryRepository inventory, InventoryAuthorization authorization) {
        this.accessPolicy = accessPolicy;
        this.inventory = inventory;
        this.authorization = authorization;
    }

    @Override
    public List<Inventory> list(User actor) {
        accessPolicy.requireRole(actor, Role.SELLER, Role.LOGISTICS_OPERATOR, Role.ADMINISTRATOR, Role.SUPERVISOR);
        if (actor.getRole() != Role.SELLER) {
            return inventory.findAll();
        }
        String sellerId = authorization.sellerOf(actor).getId();
        return inventory.findAll().stream()
                .filter(item -> authorization.productOf(item).belongsTo(sellerId))
                .toList();
    }

    @Override
    public List<InventoryMovement> movements(User actor, String inventoryId) {
        accessPolicy.requireRole(actor, Role.SELLER, Role.LOGISTICS_OPERATOR, Role.ADMINISTRATOR, Role.SUPERVISOR);
        Inventory item = authorization.find(inventoryId);
        if (actor.getRole() == Role.SELLER) {
            authorization.requireOnInventory(actor, item);
        }
        return inventory.findMovements(inventoryId);
    }
}
