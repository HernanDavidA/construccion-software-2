package com.nexusmarket.application.logistics.port.in;

import com.nexusmarket.domain.logistics.Shipment;

public interface CreateShipmentUseCase {

    Shipment execute(String actorId, String shipmentId, String orderId);
}
