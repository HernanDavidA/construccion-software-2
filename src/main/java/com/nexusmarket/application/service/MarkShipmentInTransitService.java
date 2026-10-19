package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.MarkShipmentInTransitUseCase;
import com.nexusmarket.domain.port.outbound.ShipmentRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class MarkShipmentInTransitService implements MarkShipmentInTransitUseCase {

    private final AccessPolicy accessPolicy;
    private final ShipmentRepository shipments;

    public MarkShipmentInTransitService(AccessPolicy accessPolicy, ShipmentRepository shipments) {
        this.accessPolicy = accessPolicy;
        this.shipments = shipments;
    }

    @Override
    public Shipment mark(User actor, String shipmentId) {
        accessPolicy.requireRole(actor, Role.LOGISTICS_OPERATOR);
        Shipment shipment = shipments.findById(shipmentId).orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
        shipment.markInTransit();
        return shipments.save(shipment);
    }
}
