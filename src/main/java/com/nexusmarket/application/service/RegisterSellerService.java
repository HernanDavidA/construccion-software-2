package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterSeller;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.SellerOnboarding;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.inbound.RegisterSellerUseCase;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterSellerService implements RegisterSellerUseCase {

    private final AccessPolicy accessPolicy;
    private final SellerRepository sellers;
    private final WarehouseRepository warehouses;
    private final RegisterUserService registerUserService;

    public RegisterSellerService(
            AccessPolicy accessPolicy,
            SellerRepository sellers,
            WarehouseRepository warehouses,
            RegisterUserService registerUserService) {
        this.accessPolicy = accessPolicy;
        this.sellers = sellers;
        this.warehouses = warehouses;
        this.registerUserService = registerUserService;
    }

    @Override
    public SellerOnboarding register(User actor, RegisterSeller command) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        registerUserService.requireAvailableIdentity(command.email(), command.identityDocument());
        Seller seller = new Seller(
                UUID.randomUUID().toString(),
                command.fullName(),
                command.email(),
                command.identityDocument(),
                UserStatus.ACTIVE,
                command.tradeName());
        Warehouse warehouse = new Warehouse(
                UUID.randomUUID().toString(),
                command.warehouseName(),
                WarehouseType.SELLER,
                seller.getId(),
                command.warehouseAddress());
        sellers.save(seller);
        warehouses.save(warehouse);
        return new SellerOnboarding(seller, warehouse);
    }
}
