package com.nexusmarket.domain.catalog;

import com.nexusmarket.domain.users.Seller;

import java.util.Objects;

/**
 * Physical storage space (OBJ-04). A seller warehouse requires an associated seller.
 */
public class Warehouse {

    private final String id;
    private String name;
    private final WarehouseType type;
    private final Seller seller;
    private String location;

    public Warehouse(String id, String name, WarehouseType type, Seller seller, String location) {
        this.id = requireText(id, "id");
        this.name = requireText(name, "name");
        this.type = Objects.requireNonNull(type, "type is required");
        this.location = requireText(location, "location");
        if (type == WarehouseType.SELLER) {
            this.seller = Objects.requireNonNull(seller, "a seller warehouse requires a seller");
        } else {
            this.seller = null;
        }
    }

    public static Warehouse ofMarketplace(String id, String name, String location) {
        return new Warehouse(id, name, WarehouseType.MARKETPLACE, null, location);
    }

    public static Warehouse ofSeller(String id, String name, Seller seller, String location) {
        return new Warehouse(id, name, WarehouseType.SELLER, seller, location);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "name");
    }

    public WarehouseType getType() {
        return type;
    }

    public Seller getSeller() {
        return seller;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = requireText(location, "location");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " is required");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return trimmed;
    }
}
