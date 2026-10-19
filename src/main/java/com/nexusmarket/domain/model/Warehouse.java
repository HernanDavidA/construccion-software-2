package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

public class Warehouse {

    private final String id;
    private final String name;
    private final WarehouseType type;
    private final String sellerId;
    private final Address address;

    public Warehouse(String id, String name, WarehouseType type, String sellerId, Address address) {
        this.id = RequiredText.require(id, "El identificador de la bodega es obligatorio");
        this.name = RequiredText.require(name, "El nombre de la bodega es obligatorio");
        if (type == null) {
            throw new BusinessRuleException("El tipo de bodega es obligatorio");
        }
        if (address == null || address.line() == null || address.line().isBlank()) {
            throw new BusinessRuleException("La bodega requiere una dirección");
        }
        if (type == WarehouseType.SELLER) {
            this.sellerId = RequiredText.require(sellerId, "La bodega de vendedor debe pertenecer a un vendedor");
        } else if (sellerId != null && !sellerId.isBlank()) {
            throw new BusinessRuleException("La bodega del marketplace no pertenece a un vendedor");
        } else {
            this.sellerId = null;
        }
        this.type = type;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public WarehouseType getType() {
        return type;
    }

    public String getSellerId() {
        return sellerId;
    }

    public Address getAddress() {
        return address;
    }
}
