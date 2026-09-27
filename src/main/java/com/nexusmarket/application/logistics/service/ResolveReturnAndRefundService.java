package com.nexusmarket.application.logistics.service;

import com.nexusmarket.application.logistics.port.in.ResolveReturnAndRefundUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.inventory.port.out.InventoryRepository;
import com.nexusmarket.application.logistics.port.out.ReturnRequestRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.commerce.OrderItem;
import com.nexusmarket.domain.inventory.Inventory;
import com.nexusmarket.domain.logistics.Refund;
import com.nexusmarket.domain.logistics.ReturnRequest;
import com.nexusmarket.domain.users.Administrator;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Administrator approves or rejects a return and, if approved, processes or rejects the refund (OBJ-11).
 */
public class ResolveReturnAndRefundService implements ResolveReturnAndRefundUseCase {

    private final ReturnRequestRepository returns;
    private final OrderRepository orders;
    private final InventoryRepository inventories;
    private final UserRepository users;

    public ResolveReturnAndRefundService(ReturnRequestRepository returns, OrderRepository orders,
                                         InventoryRepository inventories, UserRepository users) {
        this.returns = Objects.requireNonNull(returns);
        this.orders = Objects.requireNonNull(orders);
        this.inventories = Objects.requireNonNull(inventories);
        this.users = Objects.requireNonNull(users);
    }

    public ReturnRequest execute(String administratorId, String returnId, boolean approve,
                                 boolean processRefund, String refundId) {
        User actor = users.findById(administratorId)
                .orElseThrow(() -> new IllegalArgumentException("administrator not found"));
        if (!(actor instanceof Administrator administrator)) {
            throw new IllegalStateException("only an administrator can resolve a return");
        }
        if (!administrator.isActive()) {
            throw new IllegalStateException("the administrator is not active");
        }
        ReturnRequest returnRequest = returns.findById(returnId)
                .orElseThrow(() -> new IllegalArgumentException("return not found"));
        if (!approve) {
            returnRequest.reject();
            returns.save(returnRequest);
            return returnRequest;
        }
        returnRequest.approve();
        Refund refund = returnRequest.createRefund(refundId);
        if (processRefund) {
            refund.process();
            restockPhysicalItems(returnRequest);
        } else {
            refund.reject();
        }
        returns.save(returnRequest);
        orders.save(returnRequest.getOrder());
        return returnRequest;
    }

    private void restockPhysicalItems(ReturnRequest returnRequest) {
        for (OrderItem item : returnRequest.getOrder().getItems()) {
            if (!item.isPhysical()) {
                continue;
            }
            Inventory inventory = inventories.findByProductId(item.getProductId()).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("inventory not found for returned product"));
            inventory.returnStock(item.getQuantity());
            inventories.save(inventory);
        }
    }
}
