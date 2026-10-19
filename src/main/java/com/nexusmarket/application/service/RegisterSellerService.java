package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterSeller;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.SellerOnboarding;
import com.nexusmarket.domain.model.User;
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
        User user = registerUserService.register(command.fullName(), command.email(), command.identityDocument(), Role.SELLER);
        Seller seller = sellers.save(new Seller(
                UUID.randomUUID().toString(),
                user.getId(),
                command.tradeName(),
                user.getIdentityDocument()));
        Warehouse warehouse = warehouses.save(new Warehouse(
                UUID.randomUUID().toString(),
                command.warehouseName(),
                WarehouseType.SELLER,
                seller.getId(),
                command.warehouseAddress()));
        return new SellerOnboarding(seller, warehouse);
    }
}
