package com.nexusmarket.application.service;

import com.nexusmarket.domain.command.RegisterBuyer;
import com.nexusmarket.domain.command.RegisterSeller;
import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Seller;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.model.WarehouseType;
import com.nexusmarket.domain.port.outbound.BuyerRepository;
import com.nexusmarket.domain.port.outbound.SellerRepository;
import com.nexusmarket.domain.port.outbound.UserRepository;
import com.nexusmarket.domain.port.outbound.WarehouseRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationServiceTest {

    @Test
    void invalidBuyerAddressLeavesEmailFreeForALaterRegistration() {
        UserStore store = new UserStore();
        RegisterBuyerService service = new RegisterBuyerService(new Buyers(store), new RegisterUserService(new Users(store)));

        assertThrows(BusinessRuleException.class, () -> service.register(new RegisterBuyer("Ana", "ana@x.com", "DOC-1", null, List.of())));
        assertThrows(BusinessRuleException.class, () -> service.register(new RegisterBuyer(
                "Ana", "ana@x.com", "DOC-1", new Address(" ", "Medellin", null, null), List.of())));
        assertFalse(store.existsByEmail("ana@x.com"));
        assertFalse(store.existsByIdentityDocument("DOC-1"));

        Buyer buyer = service.register(new RegisterBuyer(
                "Ana", "ana@x.com", "DOC-1", new Address("Calle 1", "Medellin", null, null), List.of()));

        assertEquals("ana@x.com", buyer.getEmail());
        assertTrue(store.existsByEmail("ana@x.com"));
        assertEquals("Calle 1", ((Buyer) store.copy(buyer.getId())).getPrimaryAddress().line());
    }

    @Test
    void invalidSellerWarehouseLeavesNoUserSellerOrWarehouse() {
        UserStore store = new UserStore();
        MemoryWarehouses warehouses = new MemoryWarehouses();
        RegisterSellerService service = new RegisterSellerService(
                new AccessPolicy(), new Sellers(store), warehouses, new RegisterUserService(new Users(store)));
        User admin = new User("admin", "Admin", "admin@nexusmarket.local", "900", Role.ADMINISTRATOR, UserStatus.ACTIVE);

        assertThrows(BusinessRuleException.class, () -> service.register(
                admin, new RegisterSeller("Dueño", "v@x.com", "DOC-2", "Tienda", "Bodega", null)));

        assertFalse(store.existsByEmail("v@x.com"));
        assertTrue(store.isEmpty());
        assertTrue(warehouses.findAll().isEmpty());

        var onboarding = service.register(
                admin,
                new RegisterSeller("Dueño", "v@x.com", "DOC-2", "Tienda", "Bodega", new Address("Calle 2", "Medellin", null, null)));

        assertEquals(WarehouseType.SELLER, onboarding.warehouse().getType());
        assertEquals(onboarding.seller().getId(), onboarding.warehouse().getSellerId());
        assertTrue(store.existsByEmail("v@x.com"));
        assertEquals(1, warehouses.findAll().size());
    }

    private static final class UserStore {

        private final Map<String, User> users = new LinkedHashMap<>();

        User save(User user) {
            User stored = copy(user);
            users.put(stored.getId(), stored);
            return copy(stored);
        }

        Optional<User> findById(String id) {
            return Optional.ofNullable(users.get(id)).map(this::copy);
        }

        boolean existsByEmail(String email) {
            return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
        }

        boolean existsByIdentityDocument(String identityDocument) {
            return users.values().stream().anyMatch(user -> user.getIdentityDocument().equals(identityDocument));
        }

        boolean isEmpty() {
            return users.isEmpty();
        }

        List<User> values() {
            return users.values().stream().map(this::copy).toList();
        }

        User copy(String id) {
            return copy(users.get(id));
        }

        private User copy(User user) {
            if (user instanceof Buyer buyer) {
                return new Buyer(
                        buyer.getId(),
                        buyer.getFullName(),
                        buyer.getEmail(),
                        buyer.getIdentityDocument(),
                        buyer.getStatus(),
                        buyer.getPrimaryAddress(),
                        buyer.getAdditionalAddresses(),
                        buyer.getCommercialStatus());
            }
            if (user instanceof Seller seller) {
                return new Seller(
                        seller.getId(),
                        seller.getFullName(),
                        seller.getEmail(),
                        seller.getIdentityDocument(),
                        seller.getStatus(),
                        seller.getTradeName());
            }
            return new User(user.getId(), user.getFullName(), user.getEmail(), user.getIdentityDocument(), user.getRole(), user.getStatus());
        }
    }

    private static final class Users implements UserRepository {

        private final UserStore store;

        private Users(UserStore store) {
            this.store = store;
        }

        @Override
        public User save(User user) {
            return store.save(user);
        }

        @Override
        public Optional<User> findById(String id) {
            return store.findById(id);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return store.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return store.existsByEmail(email);
        }

        @Override
        public boolean existsByIdentityDocument(String identityDocument) {
            return store.existsByIdentityDocument(identityDocument);
        }

        @Override
        public List<User> findAll() {
            return store.values();
        }
    }

    private static final class Buyers implements BuyerRepository {

        private final UserStore store;

        private Buyers(UserStore store) {
            this.store = store;
        }

        @Override
        public Buyer save(Buyer buyer) {
            return (Buyer) store.save(buyer);
        }

        @Override
        public Optional<Buyer> findById(String id) {
            return store.findById(id).filter(Buyer.class::isInstance).map(Buyer.class::cast);
        }

        @Override
        public List<Buyer> findAll() {
            return store.values().stream().filter(Buyer.class::isInstance).map(Buyer.class::cast).toList();
        }
    }

    private static final class Sellers implements SellerRepository {

        private final UserStore store;

        private Sellers(UserStore store) {
            this.store = store;
        }

        @Override
        public Seller save(Seller seller) {
            return (Seller) store.save(seller);
        }

        @Override
        public Optional<Seller> findById(String id) {
            return store.findById(id).filter(Seller.class::isInstance).map(Seller.class::cast);
        }

        @Override
        public List<Seller> findAll() {
            return store.values().stream().filter(Seller.class::isInstance).map(Seller.class::cast).toList();
        }
    }

    private static final class MemoryWarehouses implements WarehouseRepository {

        private final Map<String, Warehouse> warehouses = new LinkedHashMap<>();

        @Override
        public Warehouse save(Warehouse warehouse) {
            Warehouse stored = copy(warehouse);
            warehouses.put(stored.getId(), stored);
            return copy(stored);
        }

        @Override
        public Optional<Warehouse> findById(String id) {
            return Optional.ofNullable(warehouses.get(id)).map(MemoryWarehouses::copy);
        }

        @Override
        public List<Warehouse> findAll() {
            return warehouses.values().stream().map(MemoryWarehouses::copy).toList();
        }

        @Override
        public List<Warehouse> findBySellerId(String sellerId) {
            return List.of();
        }

        private static Warehouse copy(Warehouse warehouse) {
            return new Warehouse(warehouse.getId(), warehouse.getName(), warehouse.getType(), warehouse.getSellerId(), warehouse.getAddress());
        }
    }
}
