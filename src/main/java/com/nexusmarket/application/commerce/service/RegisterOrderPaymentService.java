package com.nexusmarket.application.commerce.service;

import com.nexusmarket.application.commerce.port.in.RegisterOrderPaymentUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.domain.commerce.Invoice;
import com.nexusmarket.domain.commerce.Order;

import java.util.Objects;

/**
 * Registers payment and issues the invoice. Physical stock was already reserved at confirmation (OBJ-09).
 */
public class RegisterOrderPaymentService implements RegisterOrderPaymentUseCase {

    private final OrderRepository orders;

    public RegisterOrderPaymentService(OrderRepository orders) {
        this.orders = Objects.requireNonNull(orders);
    }

    public Invoice execute(String orderId, String invoiceNumber) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found"));
        Invoice invoice = order.registerPayment(invoiceNumber);
        orders.save(order);
        return invoice;
    }
}
