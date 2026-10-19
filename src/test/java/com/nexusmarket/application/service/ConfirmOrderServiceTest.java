package com.nexusmarket.application.service;

import com.nexusmarket.domain.model.Address;
import com.nexusmarket.domain.model.Buyer;
import com.nexusmarket.domain.model.BuyerCommercialStatus;
import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.model.CartItem;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.model.Product;
import com.nexusmarket.domain.model.ProductStatus;
import com.nexusmarket.domain.model.ProductType;
import com.nexusmarket.domain.model.UserStatus;
import com.nexusmarket.domain.port.outbound.CartRepository;
import com.nexusmarket.domain.port.outbound.InventoryRepository;
import com.nexusmarket.domain.port.outbound.OrderRepository;
import com.nexusmarket.domain.port.outbound.ProductRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfirmOrderServiceTest {

    @Test
    void savesStockBeforeTheOrder() {
        Buyer buyer = new Buyer(
                "buyer-1",
                "Ana",
                "ana@x.com",
                "DOC-1",
                UserStatus.ACTIVE,
                new Address("Calle 1", "Medellin", null, null),
                List.of(),
                BuyerCommercialStatus.ENABLED);
        Product product = new Product(
                "product-1",
                "seller-1",
                "Mesa",
                "",
                ProductType.PHYSICAL,
                new BigDecimal("10.00"),
                List.of(),
                ProductStatus.PUBLISHED);
        Inventory stock = Inventory.create(product.getId(), null, "warehouse-1");
        stock.receive(3, "initial");
        List<String> trace = new ArrayList<>();
        RecordingInventory inventory = new RecordingInventory(stock, trace);
        RecordingOrders orders = new RecordingOrders(trace);
        RecordingCarts carts = new RecordingCarts(
                new Cart("cart-1", buyer.getId(), List.of(new CartItem(product.getId(), null, 1))),
                trace);
        ConfirmOrderService service = new ConfirmOrderService(
                new AccessPolicy(), carts, new SingleProduct(product), inventory, orders);

        service.confirm(buyer);

        assertEquals(List.of("inventory", "movement", "cart", "order"), trace);
    }

    private static final class SingleProduct implements ProductRepository {

        private final Product product;

        private SingleProduct(Product product) {
            this.product = product;
        }

        @Override
        public Product save(Product product) {
            return product;
        }

        @Override
        public Optional<Product> findById(String id) {
            return product.getId().equals(id) ? Optional.of(product) : Optional.empty();
        }

        @Override
        public List<Product> findAll() {
            return List.of(product);
        }

        @Override
        public List<Product> findByStatus(ProductStatus status) {
            return List.of();
        }

        @Override
        public List<Product> findBySellerId(String sellerId) {
            return List.of();
        }
    }

    private static final class RecordingInventory implements InventoryRepository {

        private final Map<String, Inventory> stock = new LinkedHashMap<>();
        private final List<String> saved;

        private RecordingInventory(Inventory inventory, List<String> saved) {
            this.saved = saved;
            stock.put(inventory.getId(), copy(inventory));
        }

        @Override
        public Inventory save(Inventory inventory) {
            saved.add("inventory");
            stock.put(inventory.getId(), copy(inventory));
            return copy(inventory);
        }

        @Override
        public Optional<Inventory> findById(String id) {
            return Optional.ofNullable(stock.get(id)).map(RecordingInventory::copy);
        }

        @Override
        public Optional<Inventory> findByProductVariantAndWarehouse(String productId, String variantId, String warehouseId) {
            return Optional.empty();
        }

        @Override
        public Optional<Inventory> findWithAvailability(String productId, String variantId, int minimumQuantity) {
            return stock.values().stream()
                    .filter(item -> item.getProductId().equals(productId) && item.covers(minimumQuantity))
                    .findFirst()
                    .map(RecordingInventory::copy);
        }

        @Override
        public List<Inventory> findAll() {
            return stock.values().stream().map(RecordingInventory::copy).toList();
        }

        @Override
        public InventoryMovement saveMovement(InventoryMovement movement) {
            saved.add("movement");
            return movement;
        }

        @Override
        public List<InventoryMovement> findMovements(String inventoryId) {
            return List.of();
        }

        private static Inventory copy(Inventory inventory) {
            return new Inventory(
                    inventory.getId(),
                    inventory.getProductId(),
                    inventory.getVariantId(),
                    inventory.getWarehouseId(),
                    inventory.getAvailableQuantity(),
                    inventory.getReservedQuantity(),
                    inventory.getCondition());
        }
    }

    private static final class RecordingCarts implements CartRepository {

        private Cart cart;
        private final List<String> saved;

        private RecordingCarts(Cart cart, List<String> saved) {
            this.cart = cart;
            this.saved = saved;
        }

        @Override
        public Cart save(Cart cart) {
            saved.add("cart");
            this.cart = new Cart(cart.getId(), cart.getBuyerId(), cart.getItems());
            return this.cart;
        }

        @Override
        public Optional<Cart> findByBuyerId(String buyerId) {
            return cart.getBuyerId().equals(buyerId) ? Optional.of(new Cart(cart.getId(), cart.getBuyerId(), cart.getItems())) : Optional.empty();
        }
    }

    private static final class RecordingOrders implements OrderRepository {

        private final List<String> saved;

        private RecordingOrders(List<String> saved) {
            this.saved = saved;
        }

        @Override
        public Order save(Order order) {
            saved.add("order");
            return new Order(order.getId(), order.getBuyerId(), order.getStatus(), order.getLines(), order.getCreatedAt());
        }

        @Override
        public Optional<Order> findById(String id) {
            return Optional.empty();
        }

        @Override
        public List<Order> findAll() {
            return List.of();
        }

        @Override
        public List<Order> findByBuyerId(String buyerId) {
            return List.of();
        }
    }
}
