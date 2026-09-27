package com.nexusmarket.application.logistics.port.in;

import com.nexusmarket.domain.logistics.Shipment;

public interface DispatchShipmentUseCase {

    Shipment execute(String shipmentId, String trackingNumber);
}
