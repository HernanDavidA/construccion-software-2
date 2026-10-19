package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.WarehouseDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.WarehouseMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final WarehouseMongoRepository mongo;

    public WarehouseRepositoryAdapter(WarehouseMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        return mongo.save(WarehouseDocument.from(warehouse)).toDomain();
    }

    @Override
    public Optional<Warehouse> findById(String id) {
        return mongo.findById(id).map(WarehouseDocument::toDomain);
    }

    @Override
    public List<Warehouse> findAll() {
        return mongo.findAll().stream().map(WarehouseDocument::toDomain).toList();
    }

    @Override
    public List<Warehouse> findBySellerId(String sellerId) {
        return mongo.findBySellerId(sellerId).stream().map(WarehouseDocument::toDomain).toList();
    }
}
