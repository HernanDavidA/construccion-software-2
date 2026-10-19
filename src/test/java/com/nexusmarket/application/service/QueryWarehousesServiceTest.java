package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.AccessDeniedException;
import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryWarehousesServiceTest {

    @Test
    void sellerSeesOwnWarehousesAndMarketplaceWarehouses() {
        Seller seller = seller("seller-1", "uno@x.com", "DOC-1");
        Seller other = seller("seller-2", "dos@x.com", "DOC-2");
        MemoryWarehouses warehouses = new MemoryWarehouses();
        warehouses.save(new Warehouse("own", "Propia", WarehouseType.SELLER, seller.getId(), address()));
        warehouses.save(new Warehouse("other", "Ajena", WarehouseType.SELLER, other.getId(), address()));
        warehouses.save(new Warehouse("market", "Central", WarehouseType.MARKETPLACE, null, address()));
        QueryWarehousesService service = new QueryWarehousesService(new AccessPolicy(), warehouses);

        List<String> visible = service.list(seller).stream().map(Warehouse::getId).toList();

        assertEquals(List.of("own", "market"), visible);
        assertEquals("market", service.get(seller, "market").getId());
        assertThrows(AccessDeniedException.class, () -> service.get(seller, "other"));
    }

    @Test
    void otherRolesStillListEveryWarehouse() {
        User admin = new User("admin", "Admin", "admin@nexusmarket.local", "900", Role.ADMINISTRATOR, UserStatus.ACTIVE);
        MemoryWarehouses warehouses = new MemoryWarehouses();
        warehouses.save(new Warehouse("market", "Central", WarehouseType.MARKETPLACE, null, address()));
        QueryWarehousesService service = new QueryWarehousesService(new AccessPolicy(), warehouses);

        assertEquals(1, service.list(admin).size());
        assertTrue(service.get(admin, "market").getType() == WarehouseType.MARKETPLACE);
    }

    private static Seller seller(String id, String email, String document) {
        return new Seller(id, "Vendedor", email, document, UserStatus.ACTIVE, "Tienda");
    }

    private static Address address() {
        return new Address("Calle 1", "Medellin", null, null);
    }

    private static final class MemoryWarehouses implements WarehouseRepository {

        private final Map<String, Warehouse> warehouses = new LinkedHashMap<>();

        @Override
        public Warehouse save(Warehouse warehouse) {
            warehouses.put(warehouse.getId(), warehouse);
            return warehouse;
        }

        @Override
        public Optional<Warehouse> findById(String id) {
            return Optional.ofNullable(warehouses.get(id));
        }

        @Override
        public List<Warehouse> findAll() {
            return List.copyOf(warehouses.values());
        }

        @Override
        public List<Warehouse> findBySellerId(String sellerId) {
            return List.of();
        }
    }
}
