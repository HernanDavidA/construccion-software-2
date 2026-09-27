package com.nexusmarket.application.logistics.service;

import com.nexusmarket.application.logistics.port.in.RequestReturnUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.logistics.port.out.ReturnRequestRepository;
import com.nexusmarket.application.users.port.out.UserRepository;
import com.nexusmarket.domain.commerce.Order;
import com.nexusmarket.domain.logistics.ReturnRequest;
import com.nexusmarket.domain.users.Buyer;
import com.nexusmarket.domain.users.User;

import java.util.Objects;

/**
 * Buyer requests a return on a shipped or delivered order (OBJ-11).
 */
public class RequestReturnService implements RequestReturnUseCase {

    private final ReturnRequestRepository returns;
    private final OrderRepository orders;
    private final UserRepository users;

    public RequestReturnService(ReturnRequestRepository returns, OrderRepository orders,
                                UserRepository users) {
        this.returns = Objects.requireNonNull(returns);
        this.orders = Objects.requireNonNull(orders);
        this.users = Objects.requireNonNull(users);
    }

    public ReturnRequest execute(String actorId, String returnId, String orderId, String reason) {
        User actor = users.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("buyer not found"));
        if (!(actor instanceof Buyer)) {
            throw new IllegalStateException("only the buyer can request a return");
        }
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        if (!order.getBuyer().getId().equals(actorId)) {
            throw new IllegalStateException("only the order buyer can request a return");
        }
        ReturnRequest returnRequest = new ReturnRequest(returnId, order, reason);
        returns.save(returnRequest);
        orders.save(order);
        return returnRequest;
    }
}
