package com.nexusmarket.domain.model;

import com.nexusmarket.domain.exception.BusinessRuleException;

import java.util.UUID;

public class Shipment {

    private final String id;
    private final String orderId;
    private final Address deliveryAddress;
    private ShipmentStatus status;

    public Shipment(String id, String orderId, Address deliveryAddress, ShipmentStatus status) {
        this.id = RequiredText.require(id, "El identificador del envío es obligatorio");
        this.orderId = RequiredText.require(orderId, "El envío debe estar asociado a un pedido");
        if (deliveryAddress == null) {
            throw new BusinessRuleException("El envío requiere una dirección de entrega");
        }
        if (status == null) {
            throw new BusinessRuleException("El estado del envío es obligatorio");
        }
        this.deliveryAddress = deliveryAddress;
        this.status = status;
    }

    public static Shipment prepare(String orderId, Address deliveryAddress) {
        return new Shipment(UUID.randomUUID().toString(), orderId, deliveryAddress, ShipmentStatus.PREPARING);
    }

    public void pack() {
        move(ShipmentStatus.PREPARING, ShipmentStatus.PACKED);
    }

    public void dispatch() {
        move(ShipmentStatus.PACKED, ShipmentStatus.SHIPPED);
    }

    public void markInTransit() {
        move(ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT);
    }

    public void deliver() {
        if (status != ShipmentStatus.SHIPPED && status != ShipmentStatus.IN_TRANSIT) {
            throw new BusinessRuleException("El envío solo puede entregarse después del despacho");
        }
        this.status = ShipmentStatus.DELIVERED;
    }

    private void move(ShipmentStatus expected, ShipmentStatus target) {
        if (status != expected) {
            throw new BusinessRuleException("Transición de envío no permitida: " + status + " hacia " + target);
        }
        this.status = target;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public ShipmentStatus getStatus() {
        return status;
    }
}
