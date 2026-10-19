package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BusinessRulesTest {

    @Test
    void rejectsNegativeStockAndDamagedReservation() {
        Inventory inventory = Inventory.create("product", null, "warehouse");
        inventory.receive(2, "initial");
        inventory.markCondition(InventoryCondition.DAMAGED);

        assertThrows(BusinessRuleException.class, () -> inventory.reserve(1, "order"));
        assertThrows(BusinessRuleException.class, () -> inventory.adjust(-3, "adjustment"));
    }

    @Test
    void reservationAndSaleKeepBalance() {
        Inventory inventory = Inventory.create("product", null, "warehouse");
        inventory.receive(5, "initial");
        inventory.reserve(2, "order");
        inventory.saleOutbound(2, "order");

        assertEquals(3, inventory.getAvailableQuantity());
        assertEquals(0, inventory.getReservedQuantity());
    }

    @Test
    void completedOrderCannotChange() {
        Order order = new Order(
                "order-1",
                "buyer-1",
                OrderStatus.COMPLETED,
                List.of(new OrderLine("product", null, null, 1, new BigDecimal("10.00"), ProductType.DIGITAL)),
                Instant.now());

        assertThrows(BusinessRuleException.class, () -> order.advanceTo(OrderStatus.SHIPPED));
    }

    @Test
    void paidDigitalOrderCanCompleteWithoutShipping() {
        Order order = new Order(
                "order-2",
                "buyer-1",
                OrderStatus.PENDING_PAYMENT,
                List.of(new OrderLine("product", null, null, 1, new BigDecimal("10.00"), ProductType.DIGITAL)),
                Instant.now());

        order.advanceTo(OrderStatus.PAID);
        order.advanceTo(OrderStatus.COMPLETED);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void userKeepsSingleRoleAndNormalizedEmail() {
        User user = new User("user-1", "Ana Perez", "Ana@NexusMarket.local", "123", Role.BUYER, UserStatus.ACTIVE);

        assertEquals(Role.BUYER, user.getRole());
        assertEquals("ana@nexusmarket.local", user.getEmail());
        assertThrows(BusinessRuleException.class, () -> new User("user-2", "Ana", "ana", "123", null, UserStatus.ACTIVE));
    }
}
