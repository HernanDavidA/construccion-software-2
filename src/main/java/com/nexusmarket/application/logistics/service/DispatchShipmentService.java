package com.nexusmarket.application.logistics.service;

import com.nexusmarket.application.logistics.port.in.DispatchShipmentUseCase;
import com.nexusmarket.application.commerce.port.out.OrderRepository;
import com.nexusmarket.application.logistics.port.out.ShipmentRepository;
import com.nexusmarket.domain.logistics.Shipment;

import java.util.Objects;

/**
 * Dispatches a shipment with a tracking number and moves the order to SHIPPED (OBJ-10).
 */
public class DispatchShipmentService implements DispatchShipmentUseCase {

    private final ShipmentRepository shipments;
    private final OrderRepository orders;

    public DispatchShipmentService(ShipmentRepository shipments, OrderRepository orders) {
        this.shipments = Objects.requireNonNull(shipments);
        this.orders = Objects.requireNonNull(orders);
    }

    public Shipment execute(String shipmentId, String trackingNumber) {
        Shipment shipment = shipments.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("shipment not found"));
        shipment.ship(trackingNumber);
        shipments.save(shipment);
        orders.save(shipment.getOrder());
        return shipment;
    }
}
