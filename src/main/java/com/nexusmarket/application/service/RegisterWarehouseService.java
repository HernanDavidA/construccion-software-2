package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterWarehouse;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.inbound.RegisterWarehouseUseCase;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterWarehouseService implements RegisterWarehouseUseCase {

    private final AccessPolicy accessPolicy;
    private final WarehouseRepository warehouses;
    private final SellerRepository sellers;

    public RegisterWarehouseService(AccessPolicy accessPolicy, WarehouseRepository warehouses, SellerRepository sellers) {
        this.accessPolicy = accessPolicy;
        this.warehouses = warehouses;
        this.sellers = sellers;
    }

    @Override
    public Warehouse register(User actor, RegisterWarehouse command) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        if (command.type() == WarehouseType.SELLER) {
            sellers.findById(command.sellerId()).orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));
        }
        return warehouses.save(new Warehouse(
                UUID.randomUUID().toString(),
                command.name(),
                command.type(),
                command.sellerId(),
                command.address()));
    }
}
