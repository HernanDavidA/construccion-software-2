package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warehouses")
public class WarehouseDocument {

    @Id
    private String id;
    private String name;
    private WarehouseType type;
    private String sellerId;
    private AddressDocument address;

    public static WarehouseDocument from(Warehouse warehouse) {
        return new WarehouseDocument(warehouse.getId(), warehouse.getName(), warehouse.getType(), warehouse.getSellerId(), AddressMapper.from(warehouse.getAddress()));
    }

    public Warehouse toDomain() {
        return new Warehouse(id, name, type, sellerId, AddressMapper.toDomain(address));
    }
}
