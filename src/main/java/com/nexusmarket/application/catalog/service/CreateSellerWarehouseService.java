package com.nexusmarket.application.catalog.service;

import com.nexusmarket.application.catalog.port.in.CreateSellerWarehouseUseCase;
import com.nexusmarket.application.catalog.port.out.WarehouseRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.catalog.Warehouse;
import com.nexusmarket.domain.users.Seller;
import com.nexusmarket.domain.users.SellerStatus;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Creates a seller warehouse. The seller must exist and be commercially active (OBJ-04).
 */
public class CreateSellerWarehouseService implements CreateSellerWarehouseUseCase {

    private final WarehouseRepository warehouses;
    private final UserRepository users;

    public CreateSellerWarehouseService(WarehouseRepository warehouses, UserRepository users) {
        this.warehouses = Objects.requireNonNull(warehouses);
        this.users = Objects.requireNonNull(users);
    }

    public Warehouse execute(String id, String name, String sellerId, String location) {
        User user = users.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("seller not found"));
        if (!(user instanceof Seller seller)) {
            throw new IllegalStateException("user is not a seller");
        }
        if (seller.getSellerStatus() != SellerStatus.ACTIVE) {
            throw new IllegalStateException("a suspended seller cannot own a warehouse");
        }
        Warehouse warehouse = Warehouse.ofSeller(id, name, seller, location);
        warehouses.save(warehouse);
        return warehouse;
    }
}
