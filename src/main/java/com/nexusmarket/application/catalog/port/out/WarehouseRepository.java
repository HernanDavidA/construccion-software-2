package com.nexusmarket.application.catalog.port.out;

import com.nexusmarket.domain.catalog.Warehouse;

import java.util.Optional;

public interface WarehouseRepository {

    void save(Warehouse warehouse);

    Optional<Warehouse> findById(String id);
}
