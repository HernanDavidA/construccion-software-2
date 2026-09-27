package com.nexusmarket.application.logistics.port.out;

import com.nexusmarket.domain.logistics.Shipment;

import java.util.Optional;

public interface ShipmentRepository {

    void save(Shipment shipment);

    Optional<Shipment> findById(String id);
}
