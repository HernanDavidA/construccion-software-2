package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.model.Order;
import com.nexusmarket.domain.port.inbound.ConfirmDeliveryUseCase;
import com.nexusmarket.domain.port.inbound.ConfirmOrderUseCase;
import com.nexusmarket.domain.port.inbound.QueryOrdersUseCase;
import com.nexusmarket.domain.port.inbound.RegisterPaymentUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ConfirmOrderUseCase confirmOrder;
    private final RegisterPaymentUseCase registerPayment;
    private final ConfirmDeliveryUseCase confirmDelivery;
    private final QueryOrdersUseCase queryOrders;
    private final CurrentUser currentUser;

    public OrderController(
            ConfirmOrderUseCase confirmOrder,
            RegisterPaymentUseCase registerPayment,
            ConfirmDeliveryUseCase confirmDelivery,
            QueryOrdersUseCase queryOrders,
            CurrentUser currentUser) {
        this.confirmOrder = confirmOrder;
        this.registerPayment = registerPayment;
        this.confirmDelivery = confirmDelivery;
        this.queryOrders = queryOrders;
        this.currentUser = currentUser;
    }

    @PostMapping("/confirm")
    Order confirm(HttpServletRequest request) {
        return confirmOrder.confirm(currentUser.require(request));
    }

    @PostMapping("/{id}/payment")
    Order pay(HttpServletRequest request, @PathVariable String id) {
        return registerPayment.register(currentUser.require(request), id);
    }

    @PostMapping("/{id}/delivery")
    Order deliver(HttpServletRequest request, @PathVariable String id) {
        return confirmDelivery.confirm(currentUser.require(request), id);
    }

    @GetMapping("/{id}")
    Order get(HttpServletRequest request, @PathVariable String id) {
        return queryOrders.get(currentUser.require(request), id);
    }

    @GetMapping
    List<Order> list(HttpServletRequest request) {
        return queryOrders.list(currentUser.require(request));
    }
}
