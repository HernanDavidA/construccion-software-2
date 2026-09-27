package com.nexusmarket.application.logistics.service;

import com.nexusmarket.application.logistics.port.in.ConfirmShipmentDeliveryUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.logistics.port.out.ShipmentRepository;
import com.nexusmarket.domain.logistics.Shipment;

import java.util.Objects;

/**
 * Confirms delivery and completes the related order (OBJ-10).
 */
public class ConfirmShipmentDeliveryService implements ConfirmShipmentDeliveryUseCase {

    private final ShipmentRepository shipments;
    private final OrderRepository orders;

    public ConfirmShipmentDeliveryService(ShipmentRepository shipments, OrderRepository orders) {
        this.shipments = Objects.requireNonNull(shipments);
        this.orders = Objects.requireNonNull(orders);
    }

    public Shipment execute(String shipmentId) {
        Shipment shipment = shipments.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("shipment not found"));
        shipment.confirmDelivery();
        shipments.save(shipment);
        orders.save(shipment.getOrder());
        return shipment;
    }
}
