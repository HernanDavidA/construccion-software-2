package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.logistics.port.out.ShipmentRepository;
import com.nexusmarket.domain.logistics.Shipment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryShipmentRepository implements ShipmentRepository {

    private final Map<String, Shipment> shipments = new LinkedHashMap<>();

    @Override
    public void save(Shipment shipment) {
        shipments.put(shipment.getId(), shipment);
    }

    @Override
    public Optional<Shipment> findById(String id) {
        return Optional.ofNullable(shipments.get(id));
    }
}
