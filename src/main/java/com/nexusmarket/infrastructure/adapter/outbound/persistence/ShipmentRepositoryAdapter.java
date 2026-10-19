package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ShipmentDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.ShipmentMongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ShipmentRepositoryAdapter implements ShipmentRepository {

    private final ShipmentMongoRepository mongo;

    public ShipmentRepositoryAdapter(ShipmentMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Shipment save(Shipment shipment) {
        return mongo.save(ShipmentDocument.from(shipment)).toDomain();
    }

    @Override
    public Optional<Shipment> findById(String id) {
        return mongo.findById(id).map(ShipmentDocument::toDomain);
    }

    @Override
    public Optional<Shipment> findByOrderId(String orderId) {
        return mongo.findByOrderId(orderId).map(ShipmentDocument::toDomain);
    }
}
