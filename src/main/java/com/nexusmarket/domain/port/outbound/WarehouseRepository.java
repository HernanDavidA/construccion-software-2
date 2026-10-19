package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(String id);

    List<Warehouse> findAll();

    List<Warehouse> findBySellerId(String sellerId);
}
